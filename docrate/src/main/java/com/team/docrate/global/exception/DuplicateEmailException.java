package com.team.docrate.global.exception;


import com.team.docrate.global.exception.BusinessException;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException() {
        super("이미 사용 중인 이메일입니다.");
    }
}
