package com.team.docrate.global.security.jwt;

import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;
import com.team.docrate.global.exception.InvalidRefreshTokenException;
import com.team.docrate.global.exception.InvalidTokenException;
import com.team.docrate.infra.redis.RefreshTokenRedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final UserRepository userRepository;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveAccessToken(request);

        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtTokenProvider.validateToken(accessToken);

            String tokenType = jwtTokenProvider.getTokenType(accessToken);
            if (!"access".equals(tokenType)) {
                writeUnauthorizedResponse(response, "Access Token이 아닙니다.");
                return;
            }

            var authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (InvalidTokenException e) {
            if (jwtTokenProvider.isExpiredToken(accessToken)) {
                try {
                    handleAutoReissue(request, response, accessToken);
                    filterChain.doFilter(request, response);
                } catch (Exception reissueException) {
                    SecurityContextHolder.clearContext();
                    writeUnauthorizedResponse(response, reissueException.getMessage());
                }
                return;
            }

            SecurityContextHolder.clearContext();
            writeUnauthorizedResponse(response, e.getMessage());
        }
    }

    private void handleAutoReissue(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String expiredAccessToken) {

        Claims accessClaims = jwtTokenProvider.parseClaimsAllowExpired(expiredAccessToken);
        String email = accessClaims.getSubject();

        String refreshToken = resolveRefreshToken(request);
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh Token이 존재하지 않습니다.");
        }

        jwtTokenProvider.validateToken(refreshToken);

        String refreshTokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(refreshTokenType)) {
            throw new InvalidRefreshTokenException("Refresh Token이 아닙니다.");
        }

        String refreshEmail = jwtTokenProvider.getEmail(refreshToken);
        if (!email.equals(refreshEmail)) {
            throw new InvalidRefreshTokenException("토큰 사용자 정보가 일치하지 않습니다.");
        }

        String savedRefreshToken = refreshTokenRedisService.getRefreshToken(email);
        if (savedRefreshToken == null) {
            throw new InvalidRefreshTokenException("저장된 Refresh Token이 없습니다.");
        }

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh Token이 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRefreshTokenException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        refreshTokenRedisService.saveRefreshToken(user.getEmail(), newRefreshToken);

        // accessToken 쿠키는 JWT보다 조금 더 오래 유지
        addTokenCookie(response, "accessToken", newAccessToken, 60);

        // refreshToken은 실제 만료시간 사용
        addTokenCookie(
                response,
                "refreshToken",
                newRefreshToken,
                Math.toIntExact(refreshTokenExpiration / 1000)
        );

        var authentication = jwtTokenProvider.getAuthentication(newAccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("=== 자동 재발급 성공 ===");
        System.out.println("email = " + email);
        System.out.println("newAccessToken = " + newAccessToken);
        System.out.println("newRefreshToken = " + newRefreshToken);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            System.out.println("[JWT] access token from Authorization header");
            return bearerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                System.out.println("[JWT] access token from Cookie");
                return cookie.getValue();
            }
        }

        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}