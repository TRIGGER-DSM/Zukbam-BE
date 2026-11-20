package com.example.zukbambe.global.swagger.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "swagger.auth")
public record SwaggerAuthProperties(
    String username,
    String password
) {
    // 계정이 설정되지 않은 환경(로컬, 테스트)에서는 로그인 없이 문서를 연다
    public boolean isEnabled() {
        return StringUtils.hasText(username) && StringUtils.hasText(password);
    }
}
