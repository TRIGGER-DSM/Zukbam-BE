package com.example.zukbambe.domain.user.presentation.dto.response;

import com.example.zukbambe.domain.user.domain.User;
import com.example.zukbambe.domain.user.domain.enums.Role;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    Long userId,
    String email,
    String name,
    Role role,
    Long score,
    String rank,
    long solvedCount,
    StudentInfoResponse student
) {
    public static UserProfileResponse of(User user, long solvedCount, StudentInfoResponse student) {
        return UserProfileResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getRole())
            .score(user.getScore())
            .rank(user.getRank().getName())
            .solvedCount(solvedCount)
            .student(student)
            .build();
    }
}
