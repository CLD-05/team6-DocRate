package com.team.docrate.domain.home.controller;

import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            String email = authentication.getName();

            userService.findByEmail(email).ifPresent(user -> {
                model.addAttribute("nickname", user.getNickname());
                model.addAttribute("role", user.getRole());
            });
        }

        return "index";
    }
}
