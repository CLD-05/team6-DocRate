package com.team.docrate.domain.user.dto;

import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.enumtype.UserRole;
import lombok.Builder;
import lombok.Getter;

// 로그인 성공 시 사용자 정보와 JWT 토큰을 담아 반환하는 DTO
@Getter
@Builder
public class LoginResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private UserRole role;

    // 로그인 성공 후 발급되는 토큰 (access token, refresh token)
    private String accessToken;
    private String refreshToken;

    // User 엔티티와 토큰 2개를 받아서 응답 DTO 생성
    public static LoginResponseDto of(User user, String accessToken, String refreshToken) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
