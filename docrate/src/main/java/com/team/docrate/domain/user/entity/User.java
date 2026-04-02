package com.team.docrate.domain.user.entity;

import com.team.docrate.domain.user.enumtype.UserRole;
import com.team.docrate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    // 회원 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일은 필수 + 중복 불가
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 암호화된 비밀번호 저장
    @Column(nullable = false, length = 255)
    private String password;

    // 닉네임은 필수 + 중복 불가
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    // USER / ADMIN 권한 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    // 회원가입 시 일반 사용자 계정 생성
    public static User createUser(String email, String encodedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .role(UserRole.USER)
                .build();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}