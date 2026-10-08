package com.example.zukbambe.domain.streak.domain.repository;

import com.example.zukbambe.domain.streak.domain.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreakRepository extends JpaRepository<Streak, Long> {
}
