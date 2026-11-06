package com.example.zukbambe.domain.ranking.domain.repository;

import com.example.zukbambe.domain.ranking.domain.RankingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RankingSnapshotRepository extends JpaRepository<RankingSnapshot, Long> {

    // 활동이 없는 유저도 0으로 넣는다. 끊긴 스트릭은 DB 값이 남아 있으므로 어제 이후에 푼 경우만 인정한다
    @Modifying
    @Query(
        value = """
            insert into tbl_ranking_snapshots (period, user_id, user_rank_id, xp, solved_count, streak, snapshot_at)
            select cast(:period as varchar),
                   u.user_id,
                   u.user_rank_id,
                   case when cast(:period as varchar) = 'ALL' then u.xp
                        else coalesce((
                            select sum(h.amount) from tbl_xp_histories h
                            where h.user_id = u.user_id and h.created_at >= :start and h.created_at < :now
                        ), 0)
                   end,
                   (
                       select count(*) from tbl_solved_histories sh
                       where sh.user_id = u.user_id and sh.solved_time >= :start and sh.solved_time < :now
                   ),
                   coalesce((
                       select case when s.last_solved_date >= cast(:today as date) - 1 then s.current_streak else 0 end
                       from tbl_streaks s
                       where s.user_id = u.user_id
                   ), 0),
                   :now
            from tbl_users u
            """,
        nativeQuery = true
    )
    int insertSnapshot(
        @Param("period") String period,
        @Param("start") LocalDateTime start,
        @Param("now") LocalDateTime now,
        @Param("today") LocalDate today
    );
}
