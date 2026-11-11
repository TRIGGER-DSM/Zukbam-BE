package com.example.zukbambe.domain.quest.domain;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import com.example.zukbambe.domain.solution.domain.enums.Status;
import com.example.zukbambe.domain.solution.domain.repository.SubmissionRow;

import java.util.List;

// 퀘스트 진행도를 계산하는 데 쓰는 유저의 오늘 활동
public record DailyActivity(
    List<ProblemRank> solvedRanks,
    List<SubmissionRow> submissions
) {
    public int solvedCount() {
        return solvedRanks.size();
    }

    // 난이도 순서는 XP 순서와 같다
    public int solvedCountAtLeast(ProblemRank minimum) {
        return (int) solvedRanks.stream()
            .filter(rank -> rank.getXp() >= minimum.getXp())
            .count();
    }

    public int solvedRankKinds() {
        return (int) solvedRanks.stream()
            .map(ProblemRank::getProblemRankId)
            .distinct()
            .count();
    }

    public int submissionCount() {
        return submissions.size();
    }

    // 이미 푼 문제를 다시 맞힌 제출도 센다
    public int acceptedCount() {
        return (int) submissions.stream()
            .filter(submission -> submission.getStatus() == Status.ACCEPTED)
            .count();
    }

    public int acceptedLanguageKinds() {
        return (int) submissions.stream()
            .filter(submission -> submission.getStatus() == Status.ACCEPTED)
            .map(SubmissionRow::getLanguage)
            .distinct()
            .count();
    }
}
