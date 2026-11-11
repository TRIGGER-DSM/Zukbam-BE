package com.example.zukbambe.domain.notice.domain.repository;

import com.example.zukbambe.domain.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 목록에서 작성자를 건건이 조회하지 않도록 함께 가져온다
    @Override
    @EntityGraph(attributePaths = "author")
    Page<Notice> findAll(Pageable pageable);
}
