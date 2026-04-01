package com.team.docrate.infra.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String email, String refreshToken, long expirationMillis) {
        String key = generateKey(email);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expirationMillis));
    }

    public String findByEmail(String email) {
        return redisTemplate.opsForValue().get(generateKey(email));
    }

    public void delete(String email) {
        redisTemplate.delete(generateKey(email));
    }

    public boolean exists(String email) {
        Boolean hasKey = redisTemplate.hasKey(generateKey(email));
        return Boolean.TRUE.equals(hasKey);
    }

    private String generateKey(String email) {
        return REFRESH_TOKEN_PREFIX + email;
    }
}
