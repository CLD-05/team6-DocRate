package com.team.docrate.global.exception;


import com.team.docrate.global.exception.BusinessException;

public class DuplicateNicknameException extends BusinessException {

    public DuplicateNicknameException() {
        super("이미 사용 중인 닉네임입니다.");
    }
}
