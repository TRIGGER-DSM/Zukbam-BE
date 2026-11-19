package com.example.zukbambe.domain.solution.metric;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.example.zukbambe.domain.solution.domain.enums.Language;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.service.dto.GradedSolution;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// Datadog Log Pipeline이 이 형식(logger 이름, key=value)에 의존하므로 형식이 바뀌면 깨지게 둔다
class SolutionMetricLoggerTest {

    private final SolutionMetricLogger metricLogger = new SolutionMetricLogger();
    private final Logger logger = (Logger) LoggerFactory.getLogger("metrics.solution");
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    @BeforeEach
    void setUp() {
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    @Test
    void 제출_로그를_key_value로_남긴다() {
        metricLogger.submitted(10L, 3L, Language.CPP17);

        assertThat(lastMessage()).isEqualTo("event=solution_submitted solution_id=10 problem_id=3 language=CPP17");
    }

    @Test
    void 정상_채점은_시스템_실패가_아니고_실행_시간을_ms로_남긴다() {
        GradedSolution graded = new GradedSolution(
            10L, 3L, Language.JAVA, Status.WRONG_ANSWER, 0.0125, 2048L, LocalDateTime.now()
        );

        metricLogger.graded(graded, Duration.ofMillis(1500), null);

        assertThat(lastMessage())
            .startsWith("event=grading_completed solution_id=10 problem_id=3 language=JAVA result=WRONG_ANSWER system_failure=false judge_ms=1500 turnaround_ms=")
            .endsWith(" exec_time_ms=13 memory_kb=2048")
            .doesNotContain("failure_reason");
    }

    @Test
    void 시스템_실패는_이유를_남기고_측정값이_없으면_키를_생략한다() {
        GradedSolution graded = new GradedSolution(
            10L, 3L, Language.PYTHON, Status.INTERNAL_ERROR, null, null, LocalDateTime.now()
        );

        metricLogger.graded(graded, null, GradingFailure.QUEUE_FULL);

        assertThat(lastMessage())
            .contains("result=INTERNAL_ERROR system_failure=true failure_reason=queue_full turnaround_ms=")
            .doesNotContain("judge_ms", "exec_time_ms", "memory_kb", "null");
    }

    @Test
    void 이유_없이_INTERNAL_ERROR면_채점기가_보고한_실패로_본다() {
        GradedSolution graded = new GradedSolution(
            10L, 3L, Language.C99, Status.INTERNAL_ERROR, null, null, LocalDateTime.now()
        );

        metricLogger.graded(graded, Duration.ofMillis(10), null);

        assertThat(lastMessage()).contains("system_failure=true failure_reason=judge_reported");
    }

    private String lastMessage() {
        return appender.list.getLast().getFormattedMessage();
    }
}
