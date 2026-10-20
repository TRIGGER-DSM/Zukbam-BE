package com.example.zukbambe.global.judge.judge0;

import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import com.example.zukbambe.global.judge.enums.JudgeLanguage;
import com.example.zukbambe.global.judge.enums.JudgeStatus;
import com.example.zukbambe.global.judge.exception.JudgeSubmitFailedException;
import com.example.zukbambe.global.judge.exception.JudgeTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class Judge0JudgeProviderTest {

    private static final String BASE_URL = "http://judge0";
    private static final String SUBMIT_URL = BASE_URL + "/submissions/batch?base64_encoded=true";

    private MockRestServiceServer server;
    private Judge0JudgeProvider judge0JudgeProvider;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        Judge0Properties properties = new Judge0Properties(
            BASE_URL, Duration.ZERO, Duration.ofSeconds(5), Duration.ofMillis(100)
        );
        judge0JudgeProvider = new Judge0JudgeProvider(builder.build(), properties);
    }

    @Test
    void 모든_테스트케이스가_맞으면_ACCEPTED와_시간_메모리_최댓값을_돌려준다() {
        server.expect(requestTo(SUBMIT_URL))
            .andExpect(method(HttpMethod.POST))
            .andExpect(jsonPath("$.submissions.length()").value(2))
            .andExpect(jsonPath("$.submissions[0].language_id").value(50))
            .andExpect(jsonPath("$.submissions[0].compiler_options").value("-std=c99"))
            .andExpect(jsonPath("$.submissions[0].source_code").value(base64("int main(){}")))
            .andExpect(jsonPath("$.submissions[0].stdin").value(base64("1 2")))
            .andExpect(jsonPath("$.submissions[0].expected_output").value(base64("3")))
            .andExpect(jsonPath("$.submissions[0].cpu_time_limit").value(1.5))
            .andExpect(jsonPath("$.submissions[0].memory_limit").value(131072))
            .andRespond(json("[{\"token\":\"a\"},{\"token\":\"b\"}]"));
        // a는 아직 실행 중이라 다음 polling에서 a만 다시 조회한다
        server.expect(requestTo(fetchUrl("a", "b")))
            .andRespond(json(batch(result("a", 2, null, null), result("b", 3, "0.010", 3000L))));
        server.expect(requestTo(fetchUrl("a")))
            .andRespond(json(batch(result("a", 3, "0.020", 2000L))));

        JudgeResult result = judge0JudgeProvider.judge(request(JudgeLanguage.C99, 2));

        assertThat(result).isEqualTo(new JudgeResult(JudgeStatus.ACCEPTED, 0.020, 3000L));
        server.verify();
    }

    @Test
    void 테스트케이스_순서대로_처음_실패한_결과를_돌려준다() {
        server.expect(requestTo(SUBMIT_URL))
            .andRespond(json("[{\"token\":\"a\"},{\"token\":\"b\"},{\"token\":\"c\"}]"));
        // 응답 순서와 상관없이 제출한 테스트케이스 순서로 판단한다
        server.expect(requestTo(fetchUrl("a", "b", "c")))
            .andRespond(json(batch(
                result("c", 6, null, null),
                result("b", 4, "0.010", 1000L),
                result("a", 3, "0.010", 1000L)
            )));

        JudgeResult result = judge0JudgeProvider.judge(request(JudgeLanguage.PYTHON, 3));

        assertThat(result.status()).isEqualTo(JudgeStatus.WRONG_ANSWER);
    }

    @Test
    void Judge0_status를_채점_결과로_변환한다() {
        assertStatus(5, JudgeStatus.TIME_LIMIT_EXCEEDED);
        assertStatus(6, JudgeStatus.COMPILE_ERROR);
        assertStatus(7, JudgeStatus.RUNTIME_ERROR);
        assertStatus(12, JudgeStatus.RUNTIME_ERROR);
        assertStatus(13, JudgeStatus.INTERNAL_ERROR);
        assertStatus(14, JudgeStatus.INTERNAL_ERROR);
    }

    @Test
    void 테스트케이스가_20개를_넘으면_나눠서_제출하고_조회한다() {
        List<String> tokens = IntStream.range(0, 25).mapToObj(i -> "t" + i).toList();
        server.expect(requestTo(SUBMIT_URL))
            .andExpect(jsonPath("$.submissions.length()").value(20))
            .andRespond(json(tokens(tokens.subList(0, 20))));
        server.expect(requestTo(SUBMIT_URL))
            .andExpect(jsonPath("$.submissions.length()").value(5))
            .andRespond(json(tokens(tokens.subList(20, 25))));
        server.expect(requestTo(fetchUrl(tokens.subList(0, 20).toArray(String[]::new))))
            .andRespond(json(batch(tokens.subList(0, 20).stream().map(t -> result(t, 3, "0.001", 100L)).toArray(String[]::new))));
        server.expect(requestTo(fetchUrl(tokens.subList(20, 25).toArray(String[]::new))))
            .andRespond(json(batch(tokens.subList(20, 25).stream().map(t -> result(t, 3, "0.001", 100L)).toArray(String[]::new))));

        JudgeResult result = judge0JudgeProvider.judge(request(JudgeLanguage.JAVA, 25));

        assertThat(result.status()).isEqualTo(JudgeStatus.ACCEPTED);
        server.verify();
    }

    @Test
    void 진행이_없으면_타임아웃_예외를_던진다() {
        server.expect(requestTo(SUBMIT_URL)).andRespond(json("[{\"token\":\"a\"}]"));
        server.expect(ExpectedCount.manyTimes(), requestTo(fetchUrl("a")))
            .andRespond(json(batch(result("a", 1, null, null))));

        assertThatThrownBy(() -> judge0JudgeProvider.judge(request(JudgeLanguage.PYTHON, 1)))
            .isInstanceOf(JudgeTimeoutException.class);
    }

    @Test
    void 제출이_거부되면_예외를_던진다() {
        server.expect(requestTo(SUBMIT_URL))
            .andRespond(json("[{\"language_id\":[\"language with id 999 doesn't exist\"]}]"));

        assertThatThrownBy(() -> judge0JudgeProvider.judge(request(JudgeLanguage.PYTHON, 1)))
            .isInstanceOf(JudgeSubmitFailedException.class);
    }

    @Test
    void Judge0가_에러를_응답하면_예외를_던진다() {
        server.expect(requestTo(SUBMIT_URL)).andRespond(withStatus(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> judge0JudgeProvider.judge(request(JudgeLanguage.PYTHON, 1)))
            .isInstanceOf(RuntimeException.class);
    }

    private void assertStatus(int statusId, JudgeStatus expected) {
        setUp();
        server.expect(requestTo(SUBMIT_URL)).andRespond(json("[{\"token\":\"a\"}]"));
        server.expect(requestTo(fetchUrl("a"))).andRespond(json(batch(result("a", statusId, null, null))));

        assertThat(judge0JudgeProvider.judge(request(JudgeLanguage.CPP17, 1)).status()).isEqualTo(expected);
    }

    private JudgeRequest request(JudgeLanguage language, int caseCount) {
        List<JudgeCase> cases = IntStream.range(0, caseCount)
            .mapToObj(i -> new JudgeCase("1 2", "3"))
            .toList();
        return new JudgeRequest(language, "int main(){}", cases, 1.5, 131072L);
    }

    private String fetchUrl(String... tokens) {
        return BASE_URL + "/submissions/batch?tokens=" + String.join(",", tokens)
            + "&base64_encoded=true&fields=token,status,time,memory";
    }

    private String result(String token, int statusId, String time, Long memory) {
        return "{\"token\":\"%s\",\"status\":{\"id\":%d,\"description\":\"\"},\"time\":%s,\"memory\":%s}"
            .formatted(token, statusId, time == null ? "null" : "\"" + time + "\"", memory);
    }

    private String batch(String... results) {
        return "{\"submissions\":[" + String.join(",", results) + "]}";
    }

    private String tokens(List<String> tokens) {
        return tokens.stream().map(t -> "{\"token\":\"" + t + "\"}").collect(Collectors.joining(",", "[", "]"));
    }

    private org.springframework.test.web.client.ResponseCreator json(String body) {
        return withSuccess(body, MediaType.APPLICATION_JSON);
    }

    private String base64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
