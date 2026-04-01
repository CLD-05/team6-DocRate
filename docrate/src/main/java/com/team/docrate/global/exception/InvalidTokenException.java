package com.team.docrate.global.exception;

// JWT가 유효하지 않거나 만료되었을 때 사용하는 예외
public class InvalidTokenException extends BusinessException {

    public InvalidTokenException() {
        super("유효하지 않은 토큰입니다.");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
