package com.team.docrate.domain.hospital.controller;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.service.HospitalService;
import com.team.docrate.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;
    private final UserService userService;

    // 1. 병원 목록 조회
    @GetMapping
    public String list(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Principal principal,
            HttpServletRequest request,
            Model model) {

        int pageIndex = page - 1;
        if (pageIndex < 0) {
            pageIndex = 0;
        }

        var pageable = org.springframework.data.domain.PageRequest.of(
                pageIndex, 9, Sort.Direction.DESC, "id"
        );

        Page<HospitalResponse> hospitalPage =
                hospitalService.getHospitalList(search, category, pageable);

        model.addAttribute("hospitals", hospitalPage.getContent());
        model.addAttribute("page", hospitalPage);
        model.addAttribute("totalCount", hospitalPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("category", category);

        int currentPage = hospitalPage.getNumber() + 1;
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("isLoggedIn", principal != null);
        String queryString = request.getQueryString();
        model.addAttribute("currentUrl", queryString != null
                ? request.getRequestURI() + "?" + queryString
                : request.getRequestURI());

        if (principal != null) {
            userService.findByEmail(principal.getName())
                    .ifPresent(user -> model.addAttribute("nickname", user.getNickname()));
        }

        return "hospital/hospitals";
    }

    // 2. 병원 상세 페이지
    @GetMapping("/{id}")
    public String detail(
            @PathVariable("id") Long id,
            Principal principal,
            HttpServletRequest request,
            Model model) {
        HospitalResponse hospital = hospitalService.getHospitalById(id);

        model.addAttribute("hospital", hospital);
        model.addAttribute("doctors", hospitalService.getDoctorsByHospitalId(id));
        model.addAttribute("isLoggedIn", principal != null);
        model.addAttribute("currentUrl", request.getRequestURI());

        if (principal != null) {
            userService.findByEmail(principal.getName())
                    .ifPresent(user -> model.addAttribute("nickname", user.getNickname()));
        }

        return "hospital/hospitalDetail";
    }
}