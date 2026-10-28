package com.example.zukbambe.domain.group.domain;

import com.example.zukbambe.domain.group.domain.enums.GroupRole;
import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_group_members")
@EntityListeners(AuditingEntityListener.class)
public class GroupMember implements Persistable<GroupMemberId> {

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
    @Column(nullable = false, updatable = false, name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(nullable = false, name = "role")
    @Enumerated(EnumType.STRING)
    private GroupRole role;

    public static GroupMember of(User user, Group group, GroupRole role) {
        return GroupMember.builder()
                .id(new GroupMemberId(user.getUserId(), group.getGroupId()))
                .user(user)
                .group(group)
                .role(role)
                .build();
    }

    // ID를 직접 넣는 엔티티라 save()가 merge로 SELECT를 먼저 날리지 않도록,
    // 아직 저장 전(joinedAt이 채워지기 전)이면 새 엔티티로 판단해 persist하게 한다
    @Override
    public boolean isNew() {
        return joinedAt == null;
    }

    public void promoteToOwner() {
        this.role = GroupRole.OWNER;
    }

    public void demoteToMember() {
        this.role = GroupRole.MEMBER;
    }

    public boolean isOwner() {
        return role == GroupRole.OWNER;
    }
}
