package com.team.docrate.global.security.jwt;

import com.team.docrate.global.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

    	// 1. 요청에서 JWT 추출
        String token = resolveToken(request);

        try {
            if (StringUtils.hasText(token)) {
            	// 2. 토큰 검증
                jwtTokenProvider.validateToken(token);

                // 3. access token인지 확인
                String tokenType = jwtTokenProvider.getTokenType(token);
                if (!"access".equals(tokenType)) {
                    throw new InvalidTokenException("Access Token이 아닙니다.");
                }
                
                // 4. 인증 객체 생성 후 SecurityContext에 저장
                var authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
            // 5. 다음 필터로 진행
            filterChain.doFilter(request, response);

        } catch (InvalidTokenException e) {
        	// 토큰 문제 발생 시 인증 정보 제거
            SecurityContextHolder.clearContext();
            request.setAttribute("jwtException", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    
    // 요청의 쿠키/헤더에서 토큰 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        // Authorization: Bearer xxx 형태 지원
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 쿠키에 저장된 accessToken도 지원
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}