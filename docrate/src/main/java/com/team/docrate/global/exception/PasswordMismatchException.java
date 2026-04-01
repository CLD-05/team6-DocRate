package com.team.docrate.global.exception;


import com.team.docrate.global.exception.BusinessException;

public class PasswordMismatchException extends BusinessException {

    public PasswordMismatchException() {
        super("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }
}