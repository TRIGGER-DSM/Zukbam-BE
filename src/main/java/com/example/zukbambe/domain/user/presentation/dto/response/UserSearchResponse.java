package com.example.zukbambe.domain.user.presentation.dto.response;

import com.example.zukbambe.domain.user.domain.User;
import lombok.Builder;

@Builder
public record UserSearchResponse(
    Long userId,
    String name,
    String rankName,
    StudentInfoResponse student
) {
    // 선생님은 학생 정보가 없어서 student가 null이다
    public static UserSearchResponse of(User user, StudentInfoResponse student) {
        return UserSearchResponse.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .rankName(user.getRank().getName())
            .student(student)
            .build();
    }
}
