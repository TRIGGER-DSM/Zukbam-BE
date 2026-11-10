package com.example.zukbambe.domain.xp.facade;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.repository.UserRepository;
import com.example.zukbambe.domain.xp.domain.XpHistory;
import com.example.zukbambe.domain.xp.domain.repository.XpHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class XpFacade {

    private static final String SOLVE_REASON = "문제 풀이";
    private static final String PROBLEM_RANK_CHANGE_REASON = "문제 난이도 변경";
    private static final String PROBLEM_RANK_XP_CHANGE_REASON = "난이도 XP 변경";

    private final XpHistoryRepository xpHistoryRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    // 지급량은 그 시점 문제 랭크 기준으로 기록한다. 나중에 랭크가 바뀌면 보상 이벤트로 맞춘다
    @Transactional
    public void grantForSolve(User user, Problem problem) {
        long amount = problem.getRank().getXp();
        xpHistoryRepository.save(XpHistory.of(
            user, amount, SOLVE_REASON, problem.getProblemId(), problem.getTitle(), LocalDateTime.now(clock)
        ));
        userRepository.addXp(user.getUserId(), amount);
    }

    // 문제의 랭크가 바뀌면 이미 푼 사람도 새 기준과 같은 XP를 갖도록 차액을 준다
    @Transactional
    public void compensateProblemSolvers(Problem problem, long amount) {
        xpHistoryRepository.insertForProblemSolvers(
            problem.getProblemId(), amount, PROBLEM_RANK_CHANGE_REASON, LocalDateTime.now(clock)
        );
        userRepository.addXpToProblemSolvers(problem.getProblemId(), amount);
    }

    // 랭크의 XP 값이 바뀌면 그 랭크 문제를 이미 푼 사람에게도 차액을 준다
    @Transactional
    public void compensateProblemRankSolvers(ProblemRank rank, long amount) {
        xpHistoryRepository.insertForProblemRankSolvers(
            rank.getProblemRankId(), amount, PROBLEM_RANK_XP_CHANGE_REASON, LocalDateTime.now(clock)
        );
        userRepository.addXpToProblemRankSolvers(rank.getProblemRankId(), amount);
    }
}
