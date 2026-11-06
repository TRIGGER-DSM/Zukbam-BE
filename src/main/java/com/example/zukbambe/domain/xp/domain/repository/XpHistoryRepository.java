package com.example.zukbambe.domain.xp.domain.repository;

import com.example.zukbambe.domain.xp.domain.XpHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XpHistoryRepository extends JpaRepository<XpHistory, Long> {

    Page<XpHistory> findAllByUser_UserIdOrderByCreatedAtDescXpHistoryIdDesc(Long userId, Pageable pageable);
}
