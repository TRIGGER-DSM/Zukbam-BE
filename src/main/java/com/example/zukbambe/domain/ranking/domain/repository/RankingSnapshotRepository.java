package com.example.zukbambe.domain.ranking.domain.repository;

import com.example.zukbambe.domain.ranking.domain.RankingSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 선생님 제외를 순위 계산 전에 걸어야 제외했을 때 순위가 1부터 다시 매겨진다
    @Query(
        value = """
            select ranked.position as "position", ranked.user_id as "userId", ranked.name as "name",
                   ranked.rank_name as "rankName", ranked.xp as "xp", ranked.solved_count as "solvedCount",
                   ranked.streak as "streak"
            from (
                select row_number() over (
                           order by case cast(:metric as varchar)
                                        when 'XP' then s.xp
                                        when 'SOLVED_COUNT' then s.solved_count
                                        else s.streak
                                    end desc,
                                    s.user_id
                       ) as position,
                       s.user_id, u.name, r.name as rank_name, s.xp, s.solved_count, s.streak
                from tbl_ranking_snapshots s
                join tbl_users u on u.user_id = s.user_id
                join tbl_user_ranks r on r.user_rank_id = s.user_rank_id
                where s.period = cast(:period as varchar)
                  and (:excludeTeachers = false or u.role <> 'TEACHER')
            ) ranked
            order by ranked.position
            """,
        countQuery = """
            select count(*)
            from tbl_ranking_snapshots s
            join tbl_users u on u.user_id = s.user_id
            where s.period = cast(:period as varchar)
              and (:excludeTeachers = false or u.role <> 'TEACHER')
            """,
        nativeQuery = true
    )
    Page<RankingRow> findRanking(
        @Param("metric") String metric,
        @Param("period") String period,
        @Param("excludeTeachers") boolean excludeTeachers,
        Pageable pageable
    );
}
