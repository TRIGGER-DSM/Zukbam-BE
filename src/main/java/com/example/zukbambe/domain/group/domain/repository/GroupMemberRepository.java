package com.example.zukbambe.domain.group.domain.repository;

import com.example.zukbambe.domain.group.domain.Group;
import com.example.zukbambe.domain.group.domain.GroupMember;
import com.example.zukbambe.domain.group.domain.GroupMemberId;
import com.example.zukbambe.domain.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    @EntityGraph(attributePaths = "user")
    List<GroupMember> findAllByGroupOrderByJoinedAtAsc(Group group);

    @EntityGraph(attributePaths = "group")
    List<GroupMember> findAllByUserOrderByJoinedAtDesc(User user);

    void deleteAllByGroup(Group group);
}
