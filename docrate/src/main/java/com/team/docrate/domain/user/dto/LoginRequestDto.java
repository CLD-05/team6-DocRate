package com.team.docrate.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 로그인 폼에서 입력받은 이메일, 비밀번호를 담는 DTO
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {

	// 이메일은 필수 + 이메일 형식 검사
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 비밀번호는 필수
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
