package com.team.docrate.domain.request.hospitalrequest.controller;

import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hospital-requests")
@RequiredArgsConstructor
public class HospitalRequestController {
    private final HospitalRequestService hospitalRequestService;

    // 1. 병원 등록 요청 폼 띄우기
    @GetMapping("/new")
    public String newForm() {
        return "hospital/requestForm";
    }

    // 2. 병원 등록 요청 처리
    @PostMapping
    public String create(@ModelAttribute HospitalRequestDto requestDto) {
        // [참고] 아직 로그인 유저 연동 전이라면, DB에 있는 유저 ID 1번 등을 임시로 사용해야 할 수도 있습니다.
        // hospitalRequestService.saveRequest(requestDto, loginUser);
        hospitalRequestService.saveRequest(requestDto, null); 
        return "redirect:/hospitals";
    }
}
