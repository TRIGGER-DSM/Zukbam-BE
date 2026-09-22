package com.example.zukbambe.domain.group.domain;

import java.io.Serializable;
import java.util.Objects;

public class GroupMemberId implements Serializable {

    private Long user;
    private Long group;

    protected GroupMemberId() {}

    public GroupMemberId(Long user, Long group) {
        this.user = user;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupMemberId that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, group);
    }
}
