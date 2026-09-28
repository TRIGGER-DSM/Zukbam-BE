package com.example.zukbambe.domain.group.domain;

import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_group_members")
@EntityListeners(AuditingEntityListener.class)
public class GroupMember {

    @EmbeddedId
    private GroupMemberId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "join_date")
    private LocalDateTime joinDate;

    @Column(nullable = false, name = "is_host")
    private Boolean isHost;

    public static GroupMember of(User user, Group group, Boolean isHost) {
        return GroupMember.builder()
                .id(new GroupMemberId(user.getUserId(), group.getGroupId()))
                .user(user)
                .group(group)
                .isHost(isHost)
                .build();
    }
}
