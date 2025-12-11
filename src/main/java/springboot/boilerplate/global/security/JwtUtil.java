package springboot.boilerplate.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 * 
 * JWT 토큰의 생성, 파싱, 검증 등의 기능 제공
 * Access Token과 Refresh Token의 만료 시간을 관리
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    public JwtUtil(@Value("${spring.jwt.secret}") String key,
                   @Value("${spring.jwt.accessTokenExpiration}") int accessTokenExpirationMs,
                   @Value("${spring.jwt.refreshTokenExpiration}") int refreshTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    /**
     * Access Token 생성
     * 
     * @param email 사용자 이메일
     * @param role 사용자 역할
     * @return 생성된 Access Token
     */
    public String createAccessToken(String email, String role) {
        return createJwt(email, role, accessTokenExpirationMs);
    }

    /**
     * Refresh Token 생성
     * 
     * @param email 사용자 이메일
     * @param role 사용자 역할
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(String email, String role) {
        return createJwt(email, role, refreshTokenExpirationMs);
    }

    /**
     * JWT 토큰 생성
     * 
     * @param email 사용자 이메일
     * @param role 사용자 역할
     * @param expirationMs 만료 시간 (밀리초)
     * @return 생성된 JWT 토큰
     */
    public String createJwt(String email, String role, int expirationMs) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, expirationMs);
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(cal.getTime())
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String getUsername(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /**
     * 토큰에서 사용자 권한 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 권한
     */
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * 토큰의 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 만료되었으면 true, 유효하면 false
     */
    public boolean isExpired(String token) {
        try {
            Date exp = parseClaims(token).getExpiration();
            return new Date().after(exp);
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Refresh Token 만료 시간 반환
     * 
     * @return Refresh Token 만료 시간 (밀리초)
     */
    public int getRefreshTokenExpirationMillis() {
        return refreshTokenExpirationMs;
    }

    /**
     * Access Token 만료 시간 반환
     * 
     * @return Access Token 만료 시간 (밀리초)
     */
    public int getAccessTokenExpirationMillis() {
        return accessTokenExpirationMs;
    }

    /**
     * 토큰을 파싱하여 Claims를 추출
     * 
     * @param token JWT 토큰
     * @return 파싱된 Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}