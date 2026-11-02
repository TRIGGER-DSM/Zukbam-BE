package com.example.zukbambe.domain.solution.metric;

import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.service.dto.GradedSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.StringJoiner;

/**
 * 제출·채점 지표를 key=value 로그로 남긴다.
 * Datadog Log Pipeline이 logger 이름(metrics.solution)으로 골라 keyvalue 파싱하므로,
 * 키 이름을 바꾸면 monitoring/datadog 의 파이프라인·대시보드·모니터도 함께 바꿔야 한다.
 * 값에 공백이 들어가지 않게 하고, 코드·이메일 같은 개인 정보는 남기지 않는다.
 */
@Component
public class SolutionMetricLogger {

    private static final Logger log = LoggerFactory.getLogger("metrics.solution");

    public void submitted(Long solutionId, Long problemId, Language language) {
        log.info(new Fields("solution_submitted")
            .add("solution_id", solutionId)
            .add("problem_id", problemId)
            .add("language", language)
            .toString());
    }

    public void rateLimited(Long userId) {
        log.info(new Fields("submission_rate_limited")
            .add("user_id", userId)
            .toString());
    }

    /**
     * @param judgeDuration Judge 호출에 걸린 시간. Judge를 호출하지 않았으면 null
     * @param failure       시스템 원인으로 INTERNAL_ERROR가 된 경우의 이유. 정상 채점이면 null
     */
    public void graded(GradedSolution graded, Duration judgeDuration, GradingFailure failure) {
        GradingFailure reason = failure;
        if (reason == null && graded.status() == Status.INTERNAL_ERROR) {
            reason = GradingFailure.JUDGE_REPORTED;
        }

        log.info(new Fields("grading_completed")
            .add("solution_id", graded.solutionId())
            .add("problem_id", graded.problemId())
            .add("language", graded.language())
            .add("result", graded.status())
            .add("system_failure", reason != null)
            .add("failure_reason", reason == null ? null : reason.getKey())
            .add("judge_ms", judgeDuration == null ? null : judgeDuration.toMillis())
            .add("turnaround_ms", turnaroundMillis(graded.submittedAt()))
            .add("exec_time_ms", graded.time() == null ? null : Math.round(graded.time() * 1000))
            .add("memory_kb", graded.memory())
            .toString());
    }

    // submittedAt은 JPA Auditing이 시스템 시계로 채우므로 같은 시계로 잰다
    private Long turnaroundMillis(LocalDateTime submittedAt) {
        if (submittedAt == null) {
            return null;
        }
        return Math.max(0, Duration.between(submittedAt, LocalDateTime.now()).toMillis());
    }

    private static final class Fields {
        private final StringJoiner joiner = new StringJoiner(" ");

        private Fields(String event) {
            joiner.add("event=" + event);
        }

        // 값이 없으면 키를 생략해서 Datadog facet 타입이 섞이지 않게 한다
        private Fields add(String key, Object value) {
            if (value != null) {
                joiner.add(key + "=" + value);
            }
            return this;
        }

        @Override
        public String toString() {
            return joiner.toString();
        }
    }
}
