package com.example.zukbambe.domain.group.domain.repository;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupInvitation;
import com.example.zukbambe.domain.group.domain.enums.InvitationStatus;
import com.example.zukbambe.domain.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    Optional<GroupInvitation> findByGroupAndInviteeAndStatus(Group group, User invitee, InvitationStatus status);

    @EntityGraph(attributePaths = {"group", "inviter"})
    List<GroupInvitation> findAllByInviteeAndStatusAndExpiresAtAfterOrderByCreatedAtDesc(
        User invitee, InvitationStatus status, LocalDateTime now
    );

    void deleteAllByGroup(Group group);
}
