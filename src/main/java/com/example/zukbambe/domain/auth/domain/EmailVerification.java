package com.example.zukbambe.domain.auth.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@RedisHash("email_verification")
public record EmailVerification(
    @Id String verificationId,
    String email,
    String code,
    @TimeToLive Long ttl
) {
}
