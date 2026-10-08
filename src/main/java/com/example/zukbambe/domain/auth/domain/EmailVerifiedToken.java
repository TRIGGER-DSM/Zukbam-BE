package com.example.zukbambe.domain.auth.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@RedisHash("email_verified_token")
public record EmailVerifiedToken(
    @Id String token,
    String email,
    @TimeToLive Long ttl
) {
}
