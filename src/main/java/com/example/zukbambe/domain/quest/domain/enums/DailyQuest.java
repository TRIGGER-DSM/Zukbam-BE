package com.example.zukbambe.domain.quest.domain.enums;

import com.example.zukbambe.domain.quest.domain.DailyActivity;
import com.example.zukbambe.domain.rank.domain.ProblemRank;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

@Getter
public enum DailyQuest {
    SOLVE_1(QuestDifficulty.EASY, "문제 1개 풀기", 1, DailyActivity::solvedCount),
    SOLVE_2(QuestDifficulty.EASY, "문제 2개 풀기", 2, DailyActivity::solvedCount),
    SUBMIT_1(QuestDifficulty.EASY, "1번 제출하기", 1, DailyActivity::submissionCount),
    SUBMIT_3(QuestDifficulty.EASY, "3번 제출하기", 3, DailyActivity::submissionCount),
    ACCEPTED_1(QuestDifficulty.EASY, "정답 1번 받기", 1, DailyActivity::acceptedCount),
    SOLVE_1_RANK_2_OR_ABOVE(QuestDifficulty.EASY, "%s 이상 1문제 풀기", 1, 2, DailyActivity::solvedCountAtLeast),

    SOLVE_3(QuestDifficulty.NORMAL, "문제 3개 풀기", 3, DailyActivity::solvedCount),
    SUBMIT_5(QuestDifficulty.NORMAL, "5번 제출하기", 5, DailyActivity::submissionCount),
    ACCEPTED_3(QuestDifficulty.NORMAL, "정답 3번 받기", 3, DailyActivity::acceptedCount),
    SOLVE_2_RANK_2_OR_ABOVE(QuestDifficulty.NORMAL, "%s 이상 2문제 풀기", 2, 2, DailyActivity::solvedCountAtLeast),
    SOLVE_1_RANK_3_OR_ABOVE(QuestDifficulty.NORMAL, "%s 이상 1문제 풀기", 1, 3, DailyActivity::solvedCountAtLeast),
    SOLVE_2_RANK_KINDS(QuestDifficulty.NORMAL, "서로 다른 난이도 2개 풀기", 2, DailyActivity::solvedRankKinds),

    SOLVE_5(QuestDifficulty.HARD, "문제 5개 풀기", 5, DailyActivity::solvedCount),
    SOLVE_2_RANK_3_OR_ABOVE(QuestDifficulty.HARD, "%s 이상 2문제 풀기", 2, 3, DailyActivity::solvedCountAtLeast),
    SOLVE_1_RANK_4_OR_ABOVE(QuestDifficulty.HARD, "%s 이상 1문제 풀기", 1, 4, DailyActivity::solvedCountAtLeast),
    SOLVE_1_TOP_RANK(QuestDifficulty.HARD, "%s 문제 1개 풀기", 1, DailyQuest.TOP_RANK, DailyActivity::solvedCountAtLeast),
    ACCEPTED_2_LANGUAGES(QuestDifficulty.HARD, "서로 다른 언어 2개로 정답 받기", 2, DailyActivity::acceptedLanguageKinds);

    private static final int NO_RANK = 0;
    // 문제 랭크가 몇 개든 가장 높은 랭크를 가리킨다
    private static final int TOP_RANK = Integer.MAX_VALUE;

    private final QuestDifficulty difficulty;
    // 문제 랭크 이름은 바뀔 수 있어서 %s 자리에 조회 시점의 이름을 넣는다
    private final String title;
    private final int target;
    // 기준이 되는 문제 랭크의 순서(XP 오름차순, 1부터)
    private final int rankOrder;
    private final ToIntBiFunction<DailyActivity, ProblemRank> progress;

    DailyQuest(QuestDifficulty difficulty, String title, int target, ToIntFunction<DailyActivity> progress) {
        this(difficulty, title, target, NO_RANK, (activity, rank) -> progress.applyAsInt(activity));
    }

    DailyQuest(QuestDifficulty difficulty, String title, int target, int rankOrder, ToIntBiFunction<DailyActivity, ProblemRank> progress) {
        this.difficulty = difficulty;
        this.title = title;
        this.target = target;
        this.rankOrder = rankOrder;
        this.progress = progress;
    }

    public String getTitle(List<ProblemRank> ranks) {
        return rankOrder == NO_RANK ? title : title.formatted(rankOf(ranks).getName());
    }

    public int getCurrent(DailyActivity activity, List<ProblemRank> ranks) {
        ProblemRank rank = rankOrder == NO_RANK ? null : rankOf(ranks);
        return Math.min(progress.applyAsInt(activity, rank), target);
    }

    // 문제 랭크 수보다 큰 순서는 가장 높은 랭크를 기준으로 삼는다
    private ProblemRank rankOf(List<ProblemRank> ranks) {
        return ranks.get(Math.min(rankOrder, ranks.size()) - 1);
    }

    // 난이도마다 하나씩, 모든 유저에게 같은 퀘스트를 뽑는다
    public static List<DailyQuest> of(LocalDate date) {
        return Arrays.stream(QuestDifficulty.values())
            .map(difficulty -> pick(difficulty, date.toEpochDay()))
            .toList();
    }

    // 저장하지 않아도 같은 날에는 같은 퀘스트가 나오도록 날짜로 정한다.
    // 목록을 한 바퀴 돌 때마다 순서를 다시 섞어서, 한 바퀴 안에서는 같은 퀘스트가 반복되지 않는다
    private static DailyQuest pick(QuestDifficulty difficulty, long epochDay) {
        List<DailyQuest> quests = new ArrayList<>(Arrays.stream(values())
            .filter(quest -> quest.difficulty == difficulty)
            .toList());
        long round = epochDay / quests.size();
        Collections.shuffle(quests, new Random(round * 31 + difficulty.ordinal()));
        return quests.get((int) (epochDay % quests.size()));
    }
}
