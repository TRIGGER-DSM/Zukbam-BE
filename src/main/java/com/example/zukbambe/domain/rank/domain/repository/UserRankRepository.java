package com.example.zukbambe.domain.rank.domain.repository;

import com.example.zukbambe.domain.rank.domain.UserRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRankRepository extends JpaRepository<UserRank, Long> {
    Optional<UserRank> findByName(String name);
}
