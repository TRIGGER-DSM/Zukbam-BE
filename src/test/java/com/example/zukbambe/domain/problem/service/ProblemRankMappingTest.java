package com.example.zukbambe.domain.problem.service;

import com.example.zukbambe.domain.problem.domain.repository.ProblemRepository;
import com.example.zukbambe.domain.problem.presentation.dto.request.ProblemRequest;
import com.example.zukbambe.domain.problem.presentation.dto.request.TestCaseRequest;
import com.example.zukbambe.domain.problem.presentation.dto.response.ProblemDetailResponse;
import com.example.zukbambe.domain.rank.domain.repository.ProblemRankRepository;
import com.example.zukbambe.domain.rank.exception.ProblemRankNotFoundException;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProblemRankMappingTest {

    @Autowired
    private CreateProblemService createProblemService;

    @Autowired
    private QueryProblemDetailService queryProblemDetailService;

    @Autowired
    private ProblemRankRepository problemRankRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Test
    void 문제_상세에_랭크_이름과_XP가_나온다() {
        Long id = createProblemService.execute(requestWithRankId(problemRankRepository.findByName("물 한 통").orElseThrow().getProblemRankId()));
        try {
            ProblemDetailResponse response = queryProblemDetailService.execute(id);
            assertThat(response.rank().name()).isEqualTo("물 한 통");
            assertThat(response.rank().xp()).isEqualTo(50L);
        } finally {
            // 다른 테스트가 같은 DB를 쓰므로 만든 데이터를 지운다
            testCaseRepository.deleteAll(testCaseRepository.findAllByProblemOrderByTestcaseIdAsc(problemRepository.getReferenceById(id)));
            problemRepository.deleteById(id);
        }
    }

    @Test
    void 없는_문제_랭크로_만들면_실패한다() {
        assertThatThrownBy(() -> createProblemService.execute(requestWithRankId(-1L)))
            .isInstanceOf(ProblemRankNotFoundException.class);
    }

    private ProblemRequest requestWithRankId(Long problemRankId) {
        return new ProblemRequest("title-" + UUID.randomUUID(), "content", 131072L, 1.5, problemRankId,
            List.of(new TestCaseRequest("1 2", "3", true)));
    }
}
