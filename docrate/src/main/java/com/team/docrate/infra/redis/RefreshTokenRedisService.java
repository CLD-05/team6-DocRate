package com.team.docrate.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void saveRefreshToken(String email, String refreshToken) {
        refreshTokenRedisRepository.save(email, refreshToken, refreshTokenExpiration);
    }

    public String getRefreshToken(String email) {
        return refreshTokenRedisRepository.findByEmail(email);
    }

    public void deleteRefreshToken(String email) {
        refreshTokenRedisRepository.delete(email);
    }

    public boolean existsRefreshToken(String email) {
        return refreshTokenRedisRepository.exists(email);
    }
}
