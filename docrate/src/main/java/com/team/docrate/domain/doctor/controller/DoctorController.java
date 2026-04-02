package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorListItemDto;
import com.team.docrate.domain.doctor.service.DoctorService;
import com.team.docrate.domain.user.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;

    @GetMapping("/doctors")
    public String doctorList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Principal principal,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DoctorListItemDto> doctorPage = doctorService.getDoctorList(search, pageable);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("doctorList", doctorPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("isLoggedIn", principal != null);

        if (principal != null) {
            userService.findByEmail(principal.getName())
                    .ifPresent(user -> model.addAttribute("nickname", user.getNickname()));
        }

        return "doctors/list";
    }
}