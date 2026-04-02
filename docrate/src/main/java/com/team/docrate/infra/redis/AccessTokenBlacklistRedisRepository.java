package com.team.docrate.infra.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccessTokenBlacklistRedisRepository {

    private static final String BLACKLIST_PREFIX = "blacklist:access_token:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String accessToken, long expirationMillis) {
        String key = generateKey(accessToken);
        redisTemplate.opsForValue().set(key, "logout", Duration.ofMillis(expirationMillis));
    }

    public boolean exists(String accessToken) {
        Boolean hasKey = redisTemplate.hasKey(generateKey(accessToken));
        return Boolean.TRUE.equals(hasKey);
    }

    private String generateKey(String accessToken) {
        return BLACKLIST_PREFIX + accessToken;
    }
}
