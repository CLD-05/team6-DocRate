package com.team.docrate.domain.user.controller;

import com.team.docrate.domain.user.dto.ChangePasswordRequestDto;
import com.team.docrate.domain.user.dto.LoginRequestDto;
import com.team.docrate.domain.user.dto.LoginResponseDto;
import com.team.docrate.domain.user.dto.MyPageResponseDto;
import com.team.docrate.domain.user.dto.SignupRequestDto;
import com.team.docrate.domain.user.dto.UpdateUserInfoRequestDto;
import com.team.docrate.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("signupRequestDto")) {
            model.addAttribute("signupRequestDto", new SignupRequestDto());
        }
        return "users/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid SignupRequestDto signupRequestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }

        userService.signup(signupRequestDto);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(
            @RequestHeader(value = "Referer", required = false) String referer,
            Model model
    ) {
        if (!model.containsAttribute("loginRequestDto")) {
            model.addAttribute("loginRequestDto", new LoginRequestDto());
        }

        if (referer != null && !referer.contains("/login")) {
            model.addAttribute("redirectUrl", referer);
        } else {
            model.addAttribute("redirectUrl", "/");
        }

        return "users/login";
    }

    @PostMapping("/login")
    public String login(
            @Valid LoginRequestDto loginRequestDto,
            BindingResult bindingResult,
            @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
            HttpServletResponse response,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("redirectUrl", redirectUrl);
            return "users/login";
        }

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

        // 4. 로그인 성공 후 redirectUrl이 있으면 해당 페이지로, 없으면 메인 페이지로 이동
        if (org.springframework.util.StringUtils.hasText(redirectUrl) && redirectUrl.startsWith("/")) {
            return "redirect:" + redirectUrl;
        }
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String myPage(Principal principal, Model model) {
        MyPageResponseDto myPage = userService.getMyPage(principal.getName());

        model.addAttribute("myPage", myPage);
        model.addAttribute("recentReviews", userService.getMyReviews(principal.getName()).stream().limit(3).toList());
        model.addAttribute("recentRequests", userService.getMyRequests(principal.getName()).stream().limit(3).toList());

        return "users/mypage";
    }

    @GetMapping("/mypage/reviews")
    public String myReviews(Principal principal, Model model) {
        model.addAttribute("reviewList", userService.getMyReviews(principal.getName()));
        return "users/my-reviews";
    }

    @GetMapping("/mypage/requests")
    public String myRequests(Principal principal, Model model) {
        model.addAttribute("requestList", userService.getMyRequests(principal.getName()));
        return "users/my-requests";
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}