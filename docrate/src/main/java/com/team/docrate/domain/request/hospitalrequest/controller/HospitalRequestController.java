package com.team.docrate.domain.request.hospitalrequest.controller;

import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;
import com.team.docrate.domain.user.entity.User;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-requests")
@RequiredArgsConstructor
public class HospitalRequestController {
    
    private final HospitalRequestService hospitalRequestService;

    @GetMapping("/new")
    public String newForm() {
        return "hospital/requestForm";
    }

    @PostMapping("/new")
    public String create(
        @ModelAttribute HospitalRequestDto dto,
        Principal principal // 세션 대신 스프リング 시큐리티의 Principal 사용
    ) {
        // 1. 로그인 여부 체크
        if (principal == null) {
            // 로그인이 안 되어 있으면 로그인 페이지로 보냄
            return "redirect:/login";
        }

        // 2. principal.getName() (이메일)을 넘겨서 저장
        hospitalRequestService.saveByEmail(dto, principal.getName(), null);

        // 3. 성공 시 사용자의 요청 목록 페이지로 리다이렉트
        return "redirect:/mypage/requests";
    }
}