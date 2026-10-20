package com.example.zukbambe.global.judge.judge0;

import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import com.example.zukbambe.global.judge.exception.JudgeTimeoutException;
import com.example.zukbambe.global.judge.judge0.dto.Judge0BatchRequest;
import com.example.zukbambe.global.judge.judge0.dto.Judge0BatchResponse;
import com.example.zukbambe.global.judge.judge0.dto.Judge0SubmissionRequest;
import com.example.zukbambe.global.judge.judge0.dto.Judge0SubmissionResponse;
import com.example.zukbambe.global.judge.judge0.dto.Judge0TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class Judge0JudgeProvider implements JudgeProvider {

    // Judge0 batch 요청 한 번에 보낼 수 있는 최대 개수 (MAX_SUBMISSION_BATCH_SIZE 기본값)
    private static final int BATCH_SIZE = 20;
    // status id 1(In Queue), 2(Processing)까지는 아직 채점 중
    private static final int PROCESSING = 2;

    private final RestClient judge0RestClient;
    private final Judge0Properties judge0Properties;

    @Override
    public JudgeResult judge(JudgeRequest request) {
        List<String> tokens = submit(request);
        Map<String, Judge0SubmissionResponse> finished = poll(tokens);
        return aggregate(tokens.stream().map(finished::get).toList());
    }

    private List<String> submit(JudgeRequest request) {
        Judge0Language language = Judge0Language.from(request.language());
        List<Judge0SubmissionRequest> submissions = request.cases().stream()
            .map(judgeCase -> toSubmission(request, language, judgeCase))
            .toList();

        List<String> tokens = new ArrayList<>();
        for (List<Judge0SubmissionRequest> chunk : chunk(submissions)) {
            List<Judge0TokenResponse> response = judge0RestClient.post()
                .uri("/submissions/batch?base64_encoded=true")
                .body(new Judge0BatchRequest(chunk))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

            // 형식이 잘못된 submission은 token 대신 에러 내용이 온다
            if (response == null || response.size() != chunk.size()
                || response.stream().anyMatch(token -> token.token() == null)) {
                throw new IllegalStateException("Judge0 제출에 실패했습니다: " + response);
            }
            response.forEach(token -> tokens.add(token.token()));
        }
        return tokens;
    }

    private Judge0SubmissionRequest toSubmission(JudgeRequest request, Judge0Language language, JudgeCase judgeCase) {
        return new Judge0SubmissionRequest(
            encode(request.code()),
            language.getId(),
            language.getCompilerOptions(),
            encode(judgeCase.input()),
            encode(judgeCase.expectedOutput()),
            request.timeLimit(),
            request.memoryLimit()
        );
    }

    // 끝난 테스트케이스가 idleTimeout 동안 하나도 늘지 않으면 Judge0가 멈춘 것으로 본다
    private Map<String, Judge0SubmissionResponse> poll(List<String> tokens) {
        Map<String, Judge0SubmissionResponse> finished = new HashMap<>();
        long lastProgress = System.nanoTime();

        while (finished.size() < tokens.size()) {
            sleep(judge0Properties.pollInterval());

            int before = finished.size();
            List<String> pending = tokens.stream().filter(token -> !finished.containsKey(token)).toList();
            for (List<String> chunk : chunk(pending)) {
                fetch(chunk).stream()
                    .filter(submission -> submission.status().id() > PROCESSING)
                    .forEach(submission -> finished.put(submission.token(), submission));
            }

            if (finished.size() > before) {
                lastProgress = System.nanoTime();
            } else if (Duration.ofNanos(System.nanoTime() - lastProgress).compareTo(judge0Properties.idleTimeout()) > 0) {
                throw new JudgeTimeoutException();
            }
        }
        return finished;
    }

    private List<Judge0SubmissionResponse> fetch(List<String> tokens) {
        Judge0BatchResponse response = judge0RestClient.get()
            // token은 UUID라 그대로 넣는다. 템플릿 변수로 넣으면 쉼표가 %2C로 인코딩된다
            .uri("/submissions/batch?tokens=" + String.join(",", tokens)
                + "&base64_encoded=true&fields=token,status,time,memory")
            .retrieve()
            .body(Judge0BatchResponse.class);

        if (response == null || response.submissions() == null) {
            throw new IllegalStateException("Judge0 결과 조회에 실패했습니다.");
        }
        return response.submissions();
    }

    private JudgeResult aggregate(List<Judge0SubmissionResponse> results) {
        JudgeStatus status = results.stream()
            .map(result -> toStatus(result.status().id()))
            .filter(judgeStatus -> judgeStatus != JudgeStatus.ACCEPTED)
            .findFirst()
            .orElse(JudgeStatus.ACCEPTED);

        Double time = results.stream()
            .map(Judge0SubmissionResponse::time)
            .filter(Objects::nonNull)
            .map(Double::valueOf)
            .max(Double::compare)
            .orElse(null);

        Long memory = results.stream()
            .map(Judge0SubmissionResponse::memory)
            .filter(Objects::nonNull)
            .max(Long::compare)
            .orElse(null);

        return new JudgeResult(status, time, memory);
    }

    private JudgeStatus toStatus(int statusId) {
        return switch (statusId) {
            case 3 -> JudgeStatus.ACCEPTED;
            case 4 -> JudgeStatus.WRONG_ANSWER;
            case 5 -> JudgeStatus.TIME_LIMIT_EXCEEDED;
            case 6 -> JudgeStatus.COMPILE_ERROR;
            case 7, 8, 9, 10, 11, 12 -> JudgeStatus.RUNTIME_ERROR;
            default -> JudgeStatus.INTERNAL_ERROR;
        };
    }

    private String encode(String value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("채점 대기 중 인터럽트되었습니다.", e);
        }
    }

    private static <T> List<List<T>> chunk(List<T> list) {
        return IntStream.range(0, (list.size() + BATCH_SIZE - 1) / BATCH_SIZE)
            .mapToObj(i -> list.subList(i * BATCH_SIZE, Math.min(list.size(), (i + 1) * BATCH_SIZE)))
            .toList();
    }
}
