package com.example.zukbambe.domain.user.presentation;

import com.example.zukbambe.domain.user.presentation.dto.response.UserProfileResponse;
import com.example.zukbambe.domain.user.service.QueryMyProfileService;
import com.example.zukbambe.domain.user.service.QueryUserProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryMyProfileService queryMyProfileService;
    private final QueryUserProfileService queryUserProfileService;

    /**
     * 내 프로필 조회<p>
     * XP, 랭크, 푼 문제 수를 함께 응답한다. 선생님은 학생 정보(student)가 null이다.
     */
    @GetMapping("/me")
    public UserProfileResponse getMyProfile() {
        return queryMyProfileService.execute();
    }

    /**
     * 유저 프로필 조회<p>
     * XP, 랭크, 푼 문제 수를 함께 응답한다. 선생님은 학생 정보(student)가 null이다.
     */
    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
        return queryUserProfileService.execute(userId);
    }
}
