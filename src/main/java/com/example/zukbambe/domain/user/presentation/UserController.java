package com.example.zukbambe.domain.user.presentation;

import com.example.zukbambe.domain.user.presentation.dto.response.UserProfileResponse;
import com.example.zukbambe.domain.user.service.QueryMyProfileService;
import com.example.zukbambe.domain.user.service.QueryUserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryMyProfileService queryMyProfileService;
    private final QueryUserProfileService queryUserProfileService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile() {
        return queryMyProfileService.execute();
    }

    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
        return queryUserProfileService.execute(userId);
    }
}
