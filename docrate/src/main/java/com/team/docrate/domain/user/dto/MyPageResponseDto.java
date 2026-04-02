package com.team.docrate.domain.user.dto;

import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.enumtype.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private UserRole role;

    public static MyPageResponseDto from(User user) {
        return MyPageResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
