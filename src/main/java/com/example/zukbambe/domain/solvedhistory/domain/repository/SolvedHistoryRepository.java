package com.example.zukbambe.domain.solvedhistory.domain.repository;

import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistory;
import com.example.zukbambe.domain.solvedhistory.domain.SolvedHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SolvedHistoryRepository extends JpaRepository<SolvedHistory, SolvedHistoryId> {

    @Query("""
        select s.solvedDate
        from SolvedHistory s
        where s.id.userId = :userId
          and s.solvedDate >= :start
          and s.solvedDate < :end
        """)
    List<LocalDateTime> findSolvedDatesByUserIdAndPeriod(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}
