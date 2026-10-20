package com.example.zukbambe.domain.solution.service;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.solution.domain.Solution;
import com.example.zukbambe.domain.solution.domain.repository.SolutionRepository;
import com.example.zukbambe.domain.solution.exception.SolutionNotFoundException;
import com.example.zukbambe.domain.testcase.domain.repository.TestCaseRepository;
import com.example.zukbambe.global.config.AsyncConfig;
import com.example.zukbambe.global.judge.JudgeProvider;
import com.example.zukbambe.global.judge.dto.JudgeCase;
import com.example.zukbambe.global.judge.dto.JudgeRequest;
import com.example.zukbambe.global.judge.dto.JudgeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeSolutionService {

    private final SolutionRepository solutionRepository;
    private final TestCaseRepository testCaseRepository;
    private final JudgeProvider judgeProvider;
    private final ApplyGradingResultService applyGradingResultService;

    // polling이 길어질 수 있어서 채점 중에는 트랜잭션(DB 커넥션)을 잡지 않는다
    @Async(AsyncConfig.JUDGE_EXECUTOR)
    public void execute(Long solutionId) {
        Solution solution = solutionRepository.findWithProblemBySolutionId(solutionId)
            .orElseThrow(SolutionNotFoundException::new);
        Problem problem = solution.getProblem();
        List<JudgeCase> cases = testCaseRepository.findAllByProblemOrderByTestcaseIdAsc(problem).stream()
            .map(testCase -> new JudgeCase(testCase.getExampleRead(), testCase.getExampleWrite()))
            .toList();

        JudgeResult result;
        try {
            result = judgeProvider.judge(new JudgeRequest(
                solution.getLanguage().getJudgeLanguage(),
                solution.getCode(),
                cases,
                problem.getTimeLimit(),
                problem.getMemoryLimit()
            ));
        } catch (RuntimeException e) {
            log.error("채점에 실패했습니다. solutionId={}", solutionId, e);
            result = JudgeResult.internalError();
        }

        applyGradingResultService.execute(solutionId, result);
    }
}
