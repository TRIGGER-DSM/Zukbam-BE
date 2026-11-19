package com.example.zukbambe.domain.auth.domain.repository;

import com.example.zukbambe.domain.auth.domain.EmailVerifiedToken;
import org.springframework.data.repository.CrudRepository;

public interface EmailVerifiedTokenRepository extends CrudRepository<EmailVerifiedToken, String> {
}
