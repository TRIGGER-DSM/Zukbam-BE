package com.example.zukbambe.domain.rank.domain.repository;

import com.example.zukbambe.domain.rank.domain.ProblemRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemRankRepository extends JpaRepository<ProblemRank, Long> {
    Optional<ProblemRank> findByName(String name);

    List<ProblemRank> findAllByOrderBySortOrderAsc();
}
