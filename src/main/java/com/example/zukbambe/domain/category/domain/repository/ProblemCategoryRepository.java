package com.example.zukbambe.domain.category.domain.repository;

import com.example.zukbambe.domain.category.domain.ProblemCategory;
import com.example.zukbambe.domain.category.domain.ProblemCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProblemCategoryRepository extends JpaRepository<ProblemCategory, ProblemCategoryId> {

    // 목록에서 분류를 건건이 조회하지 않도록 분류와 함께 가져온다
    @Query("""
        select pc
        from ProblemCategory pc
        join fetch pc.category c
        where pc.id.problemId in :problemIds
        order by c.name
        """)
    List<ProblemCategory> findAllByProblemIds(@Param("problemIds") Collection<Long> problemIds);

    // 삭제 후 같은 연결을 다시 저장해도 충돌하지 않도록 바로 실행되는 삭제 쿼리를 쓴다
    @Modifying(flushAutomatically = true)
    @Query("delete from ProblemCategory pc where pc.id.problemId = :problemId")
    void deleteAllByProblemId(@Param("problemId") Long problemId);

    @Modifying(flushAutomatically = true)
    @Query("delete from ProblemCategory pc where pc.id.categoryId = :categoryId")
    void deleteAllByCategoryId(@Param("categoryId") Long categoryId);
}
