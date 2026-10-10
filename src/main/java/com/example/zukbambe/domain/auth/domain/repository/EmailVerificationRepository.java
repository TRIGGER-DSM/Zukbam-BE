package com.example.zukbambe.domain.auth.domain.repository;

import com.example.zukbambe.domain.auth.domain.EmailVerification;
import org.springframework.data.repository.CrudRepository;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, String> {
}
