package com.team.docrate.domain.user.controller;


import com.team.docrate.domain.user.dto.SignupRequestDto;
import com.team.docrate.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /signup → 회원가입 화면 보여주기
    @GetMapping("/signup")
    public String signupForm(Model model) {
    	// 폼과 바인딩할 DTO가 없으면 새로 생성
        if (!model.containsAttribute("signupRequestDto")) {
            model.addAttribute("signupRequestDto", new SignupRequestDto());
        }
        return "users/signup";
    }

    // POST /signup → 회원가입 처리 
    @PostMapping("/signup")
    public String signup(
            @Valid SignupRequestDto signupRequestDto,
            BindingResult bindingResult
    ) {
    	// DTO 유효성 검증 실패 시 다시 회원가입 화면으로
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }
        
        // 회원가입 처리
        userService.signup(signupRequestDto);
        
        // 성공 시 로그인 페이지로 이동
        return "redirect:/login";
    }
}