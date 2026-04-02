package com.team.docrate.domain.user.controller;


import com.team.docrate.domain.user.dto.LoginRequestDto;
import com.team.docrate.domain.user.dto.LoginResponseDto;
import com.team.docrate.domain.user.dto.SignupRequestDto;
import com.team.docrate.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
   

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
    
    // GET /login → 로그인 페이지
    @GetMapping("/login")
    public String loginForm(Model model) {
    	// 로그인 폼과 바인딩할 DTO가 없으면 새로 생성
        if (!model.containsAttribute("loginRequestDto")) {
            model.addAttribute("loginRequestDto", new LoginRequestDto());
        }
        // 로그인 페이지 반환
        return "users/login";
    }

    // POST /login → 로그인 폼 제출 시, 처리 (로그인 성공시, JWT 쿠키에 담기 + 메인페이지로 리다이렉트)
    @PostMapping("/login")
    public String login(
            @Valid LoginRequestDto loginRequestDto,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
    	// 1. 입력한 검증 실패 시 로그인 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            return "users/login";
        }
        
        // 2. 로그인 처리 (이메일/비밀번호 검증 + JWT
        LoginResponseDto loginResponse = userService.login(loginRequestDto);
        
        int accessTokenCookieMaxAge = Math.toIntExact(accessTokenExpiration / 1000);
        int refreshTokenCookieMaxAge = Math.toIntExact(refreshTokenExpiration / 1000);

        response.addCookie(createCookie(
                "accessToken",
                loginResponse.getAccessToken(),
                accessTokenCookieMaxAge
        ));

        response.addCookie(createCookie(
                "refreshToken",
                loginResponse.getRefreshToken(),
                refreshTokenCookieMaxAge
        ));

        // 4. 로그인 성공 후 메인 페이지로 이동
        return "redirect:/";
    }

    
    // JWT 토콘을 저장할 쿠키 생성
    private Cookie createCookie(String name, String value, int maxAge) {
    	
        Cookie cookie = new Cookie(name, value);
        
        cookie.setHttpOnly(true); // 자바스크립트 접근 방지
        cookie.setPath("/"); // 사이트 전체 경로에서 쿠키 사용 가능
        cookie.setMaxAge(maxAge); // 쿠키 유효 시간
        return cookie;
    }
}
