package com.example.zukbambe.domain.streak.domain.repository;

import com.example.zukbambe.domain.streak.domain.Streak;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StreakRepository extends JpaRepository<Streak, Long> {

    // 동시에 첫 풀이가 들어와도 PK 충돌 없이 한 건만 생성된다
    @Modifying
    @Query(
        value = """
            insert into tbl_streaks (user_id, current_streak, max_streak)
            values (:userId, 0, 0)
            on conflict (user_id) do nothing
            """,
        nativeQuery = true
    )
    void insertIfAbsent(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Streak> findWithLockByUserId(Long userId);
}
