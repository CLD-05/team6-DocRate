package com.team.docrate.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistService {

    private final AccessTokenBlacklistRedisRepository accessTokenBlacklistRedisRepository;

    public void blacklistAccessToken(String accessToken, long expirationMillis) {
        accessTokenBlacklistRedisRepository.save(accessToken, expirationMillis);
    }

    public boolean isBlacklisted(String accessToken) {
        return accessTokenBlacklistRedisRepository.exists(accessToken);
    }
}
