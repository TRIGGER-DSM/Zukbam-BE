package com.example.zukbambe.domain.friend.domain.repository;

import com.example.zukbambe.domain.friend.domain.Friendship;
import com.example.zukbambe.domain.friend.domain.enums.FriendshipStatus;
import com.example.zukbambe.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @EntityGraph(attributePaths = {"requester", "receiver"})
    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.requester = :user OR f.receiver = :user)
        AND f.status = :status
        """)
    Page<Friendship> findAllByUserAndStatus(
        @Param("user") User user,
        @Param("status") FriendshipStatus status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = "requester")
    Page<Friendship> findAllByReceiverAndStatus(User receiver, FriendshipStatus status, Pageable pageable);

    @Query("""
        SELECT COUNT(f) > 0 FROM Friendship f
        WHERE ((f.requester = :user AND f.receiver = :other)
            OR (f.requester = :other AND f.receiver = :user))
        AND f.status = :status
        """)
    boolean existsBetween(
        @Param("user") User user,
        @Param("other") User other,
        @Param("status") FriendshipStatus status
    );
}
