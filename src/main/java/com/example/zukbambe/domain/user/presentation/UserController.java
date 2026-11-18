package com.example.zukbambe.domain.user.presentation;

import com.example.zukbambe.domain.user.presentation.dto.response.UserProfileResponse;
import com.example.zukbambe.domain.user.presentation.dto.response.UserSearchListResponse;
import com.example.zukbambe.domain.user.service.QueryMyProfileService;
import com.example.zukbambe.domain.user.service.QueryUserProfileService;
import com.example.zukbambe.domain.user.service.QueryUserSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final QueryMyProfileService queryMyProfileService;
    private final QueryUserProfileService queryUserProfileService;
    private final QueryUserSearchService queryUserSearchService;

    /**
     * 내 프로필 조회<p>
     * XP, 랭크, 푼 문제 수도 같이 내려준다. 선생님은 학생 정보(student)가 null이다.
     */
    @GetMapping("/me")
    public UserProfileResponse getMyProfile() {
        return queryMyProfileService.execute();
    }

    /**
     * 유저 검색<p>
     * 친구를 찾을 때 쓴다. keyword가 숫자 4자리(학년, 반, 번호 두 자리, 예: 1학년 2반 5번은 1205)면 학번이 같은 학생을,
     * 아니면 이름에 keyword가 들어간 유저를 찾는다. 이름은 대소문자를 구분하지 않는다.
     * 나 자신은 결과에서 빠지고 이름순으로 나온다. 선생님은 학생 정보(student)가 null이다.
     */
    @GetMapping("/search")
    public UserSearchListResponse searchUsers(
        @RequestParam @NotBlank String keyword,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return queryUserSearchService.execute(keyword, page, size);
    }

    /**
     * 유저 프로필 조회<p>
     * XP, 랭크, 푼 문제 수도 같이 내려준다. 선생님은 학생 정보(student)가 null이다.
     */
    @GetMapping("/{userId}")
    public UserProfileResponse getUserProfile(@PathVariable Long userId) {
        return queryUserProfileService.execute(userId);
    }
}
