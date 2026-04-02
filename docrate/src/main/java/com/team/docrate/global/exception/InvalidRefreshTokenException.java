package com.team.docrate.global.exception;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException() {
        super("유효하지 않은 Refresh Token입니다.");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
