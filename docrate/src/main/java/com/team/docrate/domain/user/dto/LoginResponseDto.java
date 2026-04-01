package com.team.docrate.domain.user.dto;

import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.enumtype.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private Long userId;
    private String email;
    private String nickname;
    private UserRole role;

    private String accessToken;
    private String refreshToken;

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
