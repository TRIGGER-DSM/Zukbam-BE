package com.example.zukbambe.domain.ranking.domain.repository;

public interface RankingRow {
    Long getPosition();

    Long getUserId();

    String getName();

    String getRankName();

    Long getXp();

    Long getSolvedCount();

    Integer getStreak();
}
