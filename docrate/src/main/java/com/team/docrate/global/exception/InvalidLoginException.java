package com.team.docrate.global.exception;

public class InvalidLoginException extends BusinessException {

    public InvalidLoginException() {
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
