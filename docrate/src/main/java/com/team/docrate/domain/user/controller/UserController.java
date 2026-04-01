package com.team.docrate.domain.user.controller;

import com.team.docrate.domain.user.dto.LoginRequestDto;
import com.team.docrate.domain.user.dto.LoginResponseDto;
import com.team.docrate.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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


    @GetMapping("/login")
    public String loginForm(Model model) {
        if (!model.containsAttribute("loginRequestDto")) {
            model.addAttribute("loginRequestDto", new LoginRequestDto());
        }
        return "users/login";
    }

    @PostMapping("/login")
    public String login(
            @Valid LoginRequestDto loginRequestDto,
            BindingResult bindingResult,
            HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            return "users/login";
        }

        LoginResponseDto loginResponse = userService.login(loginRequestDto);

        response.addCookie(createCookie("accessToken", loginResponse.getAccessToken(), 60 * 30));
        response.addCookie(createCookie("refreshToken", loginResponse.getRefreshToken(), 60 * 60 * 24 * 7));

        return "redirect:/";
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
