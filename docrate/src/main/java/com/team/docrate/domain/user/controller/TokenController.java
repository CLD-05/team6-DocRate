package com.team.docrate.domain.user.controller;

import com.team.docrate.domain.user.dto.TokenReissueRequestDto;
import com.team.docrate.domain.user.dto.TokenReissueResponseDto;
import com.team.docrate.domain.user.service.UserService;
import com.team.docrate.global.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final UserService userService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @PostMapping("/token/reissue")
    public ResponseEntity<TokenReissueResponseDto> reissueToken(
            @RequestBody(required = false) TokenReissueRequestDto requestDto,
            @CookieValue(value = "refreshToken", required = false) String refreshTokenFromCookie,
            HttpServletResponse response
    ) {
    	// 확인용
    	 System.out.println("=== /token/reissue 진입 ===");
    	 System.out.println("requestDto refreshToken = " + (requestDto != null ? requestDto.getRefreshToken() : null));
    	 System.out.println("cookie refreshToken = " + refreshTokenFromCookie);
    	
    	
        String refreshToken = extractRefreshToken(requestDto, refreshTokenFromCookie);

        TokenReissueResponseDto reissueResponse = userService.reissueToken(refreshToken);

        // 확인용
        System.out.println("new accessToken = " + reissueResponse.getAccessToken());
        System.out.println("new refreshToken = " + reissueResponse.getRefreshToken());
        
        // 브라우저 쿠키 갱신
        response.addCookie(createCookie(
                "accessToken",
                reissueResponse.getAccessToken(),
                Math.toIntExact(accessTokenExpiration / 1000)
        ));
        response.addCookie(createCookie(
                "refreshToken",
                reissueResponse.getRefreshToken(),
                Math.toIntExact(refreshTokenExpiration / 1000)
        ));

        return ResponseEntity.ok(reissueResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
    }

    private String extractRefreshToken(TokenReissueRequestDto requestDto, String refreshTokenFromCookie) {
        if (requestDto != null && StringUtils.hasText(requestDto.getRefreshToken())) {
            return requestDto.getRefreshToken();
        }
        return refreshTokenFromCookie;
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}