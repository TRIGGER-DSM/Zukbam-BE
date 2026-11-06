package com.example.zukbambe.domain.user.domain.repository;

import com.example.zukbambe.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 읽고 더해서 저장하면 동시 지급 때 한쪽이 사라진다. 한 문장으로 더하고 랭크도 같은 값으로 다시 고른다
    @Modifying
    @Query(
        value = """
            update tbl_users
            set xp = xp + :amount,
                user_rank_id = (
                    select r.user_rank_id from tbl_user_ranks r
                    where r.score <= xp + :amount
                    order by r.score desc limit 1
                )
            where user_id = :userId
            """,
        nativeQuery = true
    )
    int addXp(@Param("userId") Long userId, @Param("amount") long amount);
}
