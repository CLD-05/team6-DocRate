package com.team.docrate.global.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.team.docrate.domain.user.dto.LoginRequestDto;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	// 회원가입 관련 예외는 회원가입 페이지로 다시 이동
	@ExceptionHandler({

	DuplicateEmailException.class,

	DuplicateNicknameException.class,

	PasswordMismatchException.class

	})


    public String handleSignupException(RuntimeException e, Model model) {
        model.addAttribute("signupError", e.getMessage());
        return "users/signup";
    }


    // 비즈니스 예외는 공통 에러 페이지로 이동
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/error";
    }

    // 예상하지 못한 예외는 서버 오류 메시지 출력
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
        return "error/error";
    }
}