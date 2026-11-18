package com.example.zukbambe.domain.friend.presentation.dto.response;

import com.example.zukbambe.domain.problem.domain.Problem;
import com.example.zukbambe.domain.rank.presentation.dto.response.ProblemRankResponse;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import lombok.Builder;

import java.util.List;

@Builder
public record FriendSolvedProblemResponse(
    Long problemId,
    String title,
    ProblemRankResponse rank,
    List<SolvedFriendResponse> friends
) {
    // solvedHistories는 같은 문제를 푼 친구 기록이고, 최근에 푼 순서로 정렬되어 있어야 한다
    public static FriendSolvedProblemResponse from(List<SolvedHistory> solvedHistories) {
        Problem problem = solvedHistories.getFirst().getProblem();
        return FriendSolvedProblemResponse.builder()
            .problemId(problem.getProblemId())
            .title(problem.getTitle())
            .rank(ProblemRankResponse.from(problem.getRank()))
            .friends(solvedHistories.stream().map(SolvedFriendResponse::from).toList())
            .build();
    }
}
