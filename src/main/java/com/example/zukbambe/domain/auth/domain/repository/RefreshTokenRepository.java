package com.example.zukbambe.domain.auth.domain.repository;

import com.example.zukbambe.domain.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
