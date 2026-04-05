package com.team.docrate.domain.request.hospitalrequest.controller;

import com.team.docrate.domain.hospital.repository.HospitalRepository;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-requests")
@RequiredArgsConstructor
public class HospitalRequestController {
    private final HospitalRequestService hospitalRequestService;
    private final HospitalRepository hospitalRepository;

    // 1. 병원 등록 요청 폼 띄우기
    @GetMapping("/new")
    public String newForm(Model model) {
        return "hospital/requestForm";
    }

    // 2. 병원 등록 요청 처리
    @PostMapping
    public String create(
            @ModelAttribute HospitalRequestDto requestDto,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        hospitalRequestService.saveRequest(requestDto, principal.getName());

        return "redirect:/hospitals";
    }
}
