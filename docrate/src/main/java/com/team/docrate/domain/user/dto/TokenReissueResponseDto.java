package com.team.docrate.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenReissueResponseDto {

    private String accessToken;
    private String refreshToken;

    public static TokenReissueResponseDto of(String accessToken, String refreshToken) {
        return TokenReissueResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
