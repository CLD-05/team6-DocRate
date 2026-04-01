package com.team.docrate.global.exception;

// 로그인 시 이메일 or 비밀번호가 틀렸을 때 발생하는 예외
public class InvalidLoginException extends BusinessException {

    public InvalidLoginException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
