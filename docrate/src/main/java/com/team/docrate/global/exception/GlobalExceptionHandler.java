package com.team.docrate.global.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.team.docrate.domain.user.dto.LoginRequestDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    public String handleSignupException(RuntimeException e, Model model) {
        model.addAttribute("signupError", e.getMessage());
        return "users/signup";
    }

    @ExceptionHandler(InvalidLoginException.class)
    public String handleLoginException(InvalidLoginException e, Model model) {
        model.addAttribute("loginError", e.getMessage());
        model.addAttribute("loginRequestDto", new LoginRequestDto());
        return "users/login";
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
        return "error/error";
    }
}