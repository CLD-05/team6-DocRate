package com.team.docrate.global.security.jwt;

import com.team.docrate.domain.user.repository.UserRepository;
import com.team.docrate.infra.redis.AccessTokenBlacklistService;
import com.team.docrate.infra.redis.RefreshTokenRedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final AccessTokenBlacklistService accessTokenBlacklistService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request);

        System.out.println("요청 URI = " + request.getRequestURI());
        System.out.println("accessToken = " + accessToken);

        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtTokenProvider.validateToken(accessToken)) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessTokenBlacklistService.isBlacklisted(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(
                jwtTokenProvider.getAuthentication(accessToken)
        );

        System.out.println("인증 완료 = "
                + SecurityContextHolder.getContext().getAuthentication().getName());

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}