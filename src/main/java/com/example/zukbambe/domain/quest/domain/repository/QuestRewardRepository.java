package com.example.zukbambe.domain.quest.domain.repository;

import com.example.zukbambe.domain.quest.domain.QuestReward;
import com.example.zukbambe.domain.quest.domain.enums.DailyQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface QuestRewardRepository extends JpaRepository<QuestReward, Long> {

    @Query("""
        select r.quest
        from QuestReward r
        where r.user.userId = :userId
          and r.questDate = :questDate
        """)
    List<DailyQuest> findQuestsByUserIdAndQuestDate(@Param("userId") Long userId, @Param("questDate") LocalDate questDate);

    // 확인 후 저장하면 동시에 요청했을 때 보상이 두 번 나간다. 넣은 개수(1/0)로 처음 받는 보상인지 판단한다
    @Modifying
    @Query(
        value = """
            insert into tbl_quest_rewards (user_id, quest_date, quest, xp, rewarded_at)
            values (:userId, :questDate, :quest, :xp, :rewardedAt)
            on conflict (user_id, quest_date, quest) do nothing
            """,
        nativeQuery = true
    )
    int insertIfAbsent(
        @Param("userId") Long userId,
        @Param("questDate") LocalDate questDate,
        @Param("quest") String quest,
        @Param("xp") long xp,
        @Param("rewardedAt") LocalDateTime rewardedAt
    );
}
