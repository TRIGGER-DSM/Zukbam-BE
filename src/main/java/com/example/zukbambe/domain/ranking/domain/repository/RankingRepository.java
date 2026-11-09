package com.example.zukbambe.domain.ranking.domain.repository;

import com.example.zukbambe.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RankingRepository extends Repository<User, Long> {

    // 유저 수가 학생 수로 제한되어 있어서 조회할 때마다 바로 집계한다. 그래서 XP가 바뀌면 랭킹에도 바로 보인다.
    // 활동이 없는 유저도 0으로 나온다. 끊긴 스트릭은 DB 값이 남아 있으므로 어제 이후에 푼 경우만 인정한다.
    // 선생님 제외를 순위 계산 전에 걸어야 제외했을 때 순위가 1부터 다시 매겨진다
    @Query(
        value = """
            select ranked.position as "position", ranked.user_id as "userId", ranked.name as "name",
                   ranked.rank_name as "rankName", ranked.xp as "xp", ranked.solved_count as "solvedCount",
                   ranked.streak as "streak"
            from (
                select row_number() over (
                           order by case cast(:metric as varchar)
                                        when 'XP' then t.xp
                                        when 'SOLVED_COUNT' then t.solved_count
                                        else t.streak
                                    end desc,
                                    t.user_id
                       ) as position,
                       t.*
                from (
                    select u.user_id, u.name, r.name as rank_name,
                           case when cast(:period as varchar) = 'ALL' then u.xp
                                else cast(coalesce((
                                    select sum(h.amount) from tbl_xp_histories h
                                    where h.user_id = u.user_id and h.created_at >= :start
                                ), 0) as bigint)
                           end as xp,
                           (
                               select count(*) from tbl_solved_histories sh
                               where sh.user_id = u.user_id and sh.solved_time >= :start
                           ) as solved_count,
                           coalesce((
                               select case when s.last_solved_date >= cast(:today as date) - 1 then s.current_streak else 0 end
                               from tbl_streaks s
                               where s.user_id = u.user_id
                           ), 0) as streak
                    from tbl_users u
                    join tbl_user_ranks r on r.user_rank_id = u.user_rank_id
                    where (:excludeTeachers = false or u.role <> 'TEACHER')
                ) t
            ) ranked
            order by ranked.position
            """,
        countQuery = """
            select count(*)
            from tbl_users u
            where (:excludeTeachers = false or u.role <> 'TEACHER')
            """,
        nativeQuery = true
    )
    Page<RankingRow> findRanking(
        @Param("metric") String metric,
        @Param("period") String period,
        @Param("start") LocalDateTime start,
        @Param("today") LocalDate today,
        @Param("excludeTeachers") boolean excludeTeachers,
        Pageable pageable
    );
}
