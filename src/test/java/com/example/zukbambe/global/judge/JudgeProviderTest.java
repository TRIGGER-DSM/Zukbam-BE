package com.example.zukbambe.global.judge;

import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeLanguage;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import com.example.zukbambe.global.judge.exception.JudgeSubmitFailedException;
import com.example.zukbambe.global.judge.exception.JudgeTimeoutException;
import com.example.zukbambe.global.judge.judge0.Judge0JudgeProvider;
import com.example.zukbambe.global.judge.judge0.Judge0Properties;
import com.example.zukbambe.global.judge.judge0.Judge0Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class JudgeProviderTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final List<FakeCase> fakeCases = new ArrayList<>();
    private int submittedCount;
    private boolean rejectSubmission;
    private boolean unavailable;

    private JudgeProvider judgeProvider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://judge0");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(ExpectedCount.manyTimes(), anything()).andRespond(this::respond);

        Judge0Properties properties = new Judge0Properties(
            "http://judge0", Duration.ZERO, Duration.ofSeconds(5), Duration.ofMillis(100)
        );
        judgeProvider = new Judge0JudgeProvider(builder.build(), properties);
    }

    @Test
    void 모든_테스트케이스가_맞으면_ACCEPTED와_시간_메모리_최댓값을_돌려준다() {
        givenCase("0.010", 3000L, Judge0Status.ACCEPTED);
        givenCase("0.020", 2000L, Judge0Status.ACCEPTED);

        JudgeResult result = judgeProvider.judge(request(2));

        assertThat(result).isEqualTo(new JudgeResult(JudgeStatus.ACCEPTED, 0.020, 3000L));
    }

    @Test
    void 채점_중인_테스트케이스는_끝날_때까지_기다린다() {
        givenCase("0.010", 1000L, Judge0Status.IN_QUEUE, Judge0Status.PROCESSING, Judge0Status.WRONG_ANSWER);
        givenCase("0.010", 1000L, Judge0Status.ACCEPTED);

        JudgeResult result = judgeProvider.judge(request(2));

        assertThat(result.status()).isEqualTo(JudgeStatus.WRONG_ANSWER);
    }

    @Test
    void 테스트케이스_순서대로_처음_실패한_결과를_돌려준다() {
        givenCase("0.010", 1000L, Judge0Status.ACCEPTED);
        givenCase("0.010", 1000L, Judge0Status.PROCESSING, Judge0Status.WRONG_ANSWER);
        givenCase(null, null, Judge0Status.COMPILATION_ERROR);

        JudgeResult result = judgeProvider.judge(request(3));

        assertThat(result.status()).isEqualTo(JudgeStatus.WRONG_ANSWER);
    }

    @Test
    void 실패_종류별로_채점_결과를_돌려준다() {
        assertStatus(Judge0Status.TIME_LIMIT_EXCEEDED, JudgeStatus.TIME_LIMIT_EXCEEDED);
        assertStatus(Judge0Status.COMPILATION_ERROR, JudgeStatus.COMPILE_ERROR);
        assertStatus(Judge0Status.RUNTIME_ERROR_SIGSEGV, JudgeStatus.RUNTIME_ERROR);
        assertStatus(Judge0Status.RUNTIME_ERROR_OTHER, JudgeStatus.RUNTIME_ERROR);
        assertStatus(Judge0Status.INTERNAL_ERROR, JudgeStatus.INTERNAL_ERROR);
        assertStatus(Judge0Status.EXEC_FORMAT_ERROR, JudgeStatus.INTERNAL_ERROR);
    }

    @Test
    void 테스트케이스가_많아도_전부_채점한다() {
        IntStream.range(0, 24).forEach(i -> givenCase("0.001", 100L, Judge0Status.ACCEPTED));
        givenCase("0.001", 100L, Judge0Status.TIME_LIMIT_EXCEEDED);

        JudgeResult result = judgeProvider.judge(request(25));

        assertThat(result.status()).isEqualTo(JudgeStatus.TIME_LIMIT_EXCEEDED);
    }

    @Test
    void 진행이_없으면_타임아웃_예외를_던진다() {
        givenCase(null, null, Judge0Status.IN_QUEUE);

        assertThatThrownBy(() -> judgeProvider.judge(request(1)))
            .isInstanceOf(JudgeTimeoutException.class);
    }

    @Test
    void 제출이_거부되면_예외를_던진다() {
        givenCase(null, null, Judge0Status.ACCEPTED);
        rejectSubmission = true;

        assertThatThrownBy(() -> judgeProvider.judge(request(1)))
            .isInstanceOf(JudgeSubmitFailedException.class);
    }

    @Test
    void 채점_서버가_응답하지_않으면_예외를_던진다() {
        givenCase(null, null, Judge0Status.ACCEPTED);
        unavailable = true;

        assertThatThrownBy(() -> judgeProvider.judge(request(1)))
            .isInstanceOf(RuntimeException.class);
    }

    private void assertStatus(Judge0Status judge0Status, JudgeStatus expected) {
        fakeCases.clear();
        submittedCount = 0;
        givenCase(null, null, judge0Status);

        assertThat(judgeProvider.judge(request(1)).status()).isEqualTo(expected);
    }

    // statuses는 조회할 때마다 하나씩 넘어가고, 마지막 상태에서 멈춘다
    private void givenCase(String time, Long memory, Judge0Status... statuses) {
        fakeCases.add(new FakeCase(new ArrayDeque<>(Arrays.asList(statuses)), time, memory));
    }

    private JudgeRequest request(int caseCount) {
        List<JudgeCase> cases = IntStream.range(0, caseCount)
            .mapToObj(i -> new JudgeCase("1 2", "3"))
            .toList();
        return new JudgeRequest(JudgeLanguage.PYTHON, "print(3)", cases, 1.5, 131072L);
    }

    // 테스트케이스 i번째 제출의 token은 "t{i}"
    private ClientHttpResponse respond(ClientHttpRequest request) throws IOException {
        if (unavailable) {
            return withStatus(HttpStatus.SERVICE_UNAVAILABLE).createResponse(request);
        }
        if (request.getMethod() == HttpMethod.POST) {
            return json(submitResponse((MockClientHttpRequest) request), request);
        }
        return json(fetchResponse(request), request);
    }

    private String submitResponse(MockClientHttpRequest request) {
        if (rejectSubmission) {
            return "[{\"language_id\":[\"language with id 999 doesn't exist\"]}]";
        }
        int count = objectMapper.readTree(request.getBodyAsString()).get("submissions").size();
        int start = submittedCount;
        submittedCount += count;
        return IntStream.range(start, start + count)
            .mapToObj(i -> "{\"token\":\"t" + i + "\"}")
            .collect(Collectors.joining(",", "[", "]"));
    }

    private String fetchResponse(ClientHttpRequest request) {
        String tokens = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().getFirst("tokens");
        return Arrays.stream(tokens.split(","))
            .map(token -> fakeCases.get(Integer.parseInt(token.substring(1))).poll(token))
            .collect(Collectors.joining(",", "{\"submissions\":[", "]}"));
    }

    private ClientHttpResponse json(String body, ClientHttpRequest request) throws IOException {
        return withSuccess(body, MediaType.APPLICATION_JSON).createResponse(request);
    }

    private record FakeCase(Deque<Judge0Status> statuses, String time, Long memory) {

        String poll(String token) {
            Judge0Status status = statuses.size() > 1 ? statuses.poll() : statuses.peek();
            boolean finished = status.isFinished();
            return "{\"token\":\"%s\",\"status\":{\"id\":%d,\"description\":\"\"},\"time\":%s,\"memory\":%s}".formatted(
                token,
                status.getId(),
                finished && time != null ? "\"" + time + "\"" : "null",
                finished ? memory : null
            );
        }
    }
}
