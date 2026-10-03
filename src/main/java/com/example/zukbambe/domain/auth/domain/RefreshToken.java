package com.example.zukbambe.domain.auth.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@RedisHash("refresh_token")
public record RefreshToken(
    @Id Long userId,
    String token,
    @TimeToLive Long ttl
) {
}
