package springboot.boilerplate.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    // RefreshToken 저장 (TTL 설정 포함)
    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                refreshToken,
                ttlMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 저장된 Refresh Token 조회
     * 
     * @param userId 사용자 ID
     * @return 저장된 Refresh Token, 없으면 null
     */
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
    }

    /**
     * 저장된 Refresh Token 삭제
     * 
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    /**
     * Refresh Token 키의 존재 여부 확인
     * 
     * @param userId 사용자 ID
     * @return 키가 존재하면 true, 없으면 false
     */
    public boolean hasKey(Long userId) {
        return redisTemplate.hasKey(REFRESH_TOKEN_PREFIX + userId);
    }
}