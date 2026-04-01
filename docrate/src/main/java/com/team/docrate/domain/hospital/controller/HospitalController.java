package com.team.docrate.domain.hospital.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.service.HospitalService;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;
    private final HospitalRequestService hospitalRequestService;

    // 1. 병원 목록 조회
    @GetMapping
    public String list(
        @RequestParam(value = "category", required = false) String category, 
        @PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, 
        Model model) {
        
        Page<HospitalResponse> hospitalPage = hospitalService.getHospitalList(category, pageable);
        
        model.addAttribute("hospitals", hospitalPage.getContent());
        model.addAttribute("page", hospitalPage); 
        
        int nowPage = hospitalPage.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, hospitalPage.getTotalPages());
        
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        
        return "hospital/hospitals"; 
    }

    // 2. 병원 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        HospitalResponse hospital = hospitalService.getHospitalById(id);
        model.addAttribute("hospital", hospital);
        model.addAttribute("doctors", new java.util.ArrayList<>());
        return "hospital/hospitalDetail";
    }

    // 3. 병원 등록 요청 폼 띄우기 (주소: /hospitals/request)
    @GetMapping("/request")
    public String showRequestForm() {
        // templates/hospital/requestForm.html 경로와 일치
        return "hospital/requestForm"; 
    }

    // 4. 병원 등록 요청 처리 (POST 주소: /hospitals/request)
    @PostMapping("/request")
    public String createRequest(@ModelAttribute("hospitalRequestDto") HospitalRequestDto requestDto) {
        // [참고] 아직 로그인 유저 연동 전이라면, DB에 있는 유저 ID 1번 등을 임시로 사용해야 할 수도 있습니다.
        // User loginUser = ... (현재 로그인 유저 가져오는 로직)
        // hospitalRequestService.saveRequest(requestDto, loginUser);
        
        hospitalRequestService.saveRequest(requestDto, null); // 일단 구조만 맞춤
        return "redirect:/hospitals";
    }
    
}