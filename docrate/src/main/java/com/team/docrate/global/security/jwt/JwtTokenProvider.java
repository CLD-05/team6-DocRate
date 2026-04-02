package com.team.docrate.global.security.jwt;

import com.team.docrate.domain.user.enumtype.UserRole;
import com.team.docrate.global.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

// JWT 생성 및 검증 담당 클래스
@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey key;

    // application.properties 에 있는 secret 값을 JWT SecretKey 객체로 변환
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    // 로그인 성공 시 인증용 Access Token 생성
    public String createAccessToken(String email, UserRole role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(email)					// 토큰 주인 = 이메일
                .claim("role", role.name())		// 사용자 권한 저장
                .claim("type", "access")		// access token 구분용
                .issuedAt(now)					// 발급시간
                .expiration(expiration)			// 만료시간
                .signWith(key)					// 비밀키로 서명
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(email)				// 토큰 주인 = 이메일
                .claim("type", "refresh") 	// refresh token 구분용
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);  // 파싱이 되면 정상 토큰
            return true;
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("만료된 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    // 토큰에서 Authentication 생성
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        Collection<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

 // 토큰에서 이메일 추출
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // 토큰에서 권한 추출
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // 토큰 타입 추출
    public String getTokenType(String token) {
        return parseClaims(token).get("type", String.class);
    }

    // Claims 추출 (JWT를 파싱해서 내부 claims(subject, role, type 등)를 추출)
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)			// 비밀키로 서명 검증
                .build()
                .parseSignedClaims(token)	// JWT 파싱
                .getPayload();				// claims 추출
    }
    
 // 만료된 토큰에서도 claims를 추출하기 위한 메서드
    public Claims parseClaimsAllowExpired(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}