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

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    // 1. 병원 목록 조회
    @GetMapping
    public String list(
        @RequestParam(value = "search", required = false) String search, 
        @RequestParam(value = "category", required = false) String category, 
        @RequestParam(value = "page", defaultValue = "1") int page, // 기본값을 1로 설정
        Model model) {
        
        // 사용자가 보는 페이지 번호(1부터 시작)를 Spring Data JPA의 0 기반 인덱스로 변환
        int pageIndex = page - 1;
        if (pageIndex < 0) pageIndex = 0;

        // PageRequest를 직접 생성하여 0 기반 인덱스 적용 (한 페이지당 9개)
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageIndex, 9, Sort.Direction.DESC, "id");
        
        Page<HospitalResponse> hospitalPage = hospitalService.getHospitalList(search, category, pageable);
        
        model.addAttribute("hospitals", hospitalPage.getContent());
        model.addAttribute("page", hospitalPage); 
        model.addAttribute("totalCount", hospitalPage.getTotalElements()); 
        model.addAttribute("search", search); 
        
        // 뷰에서 사용할 현재 페이지 번호 (1부터 시작)
        int currentPage = hospitalPage.getNumber() + 1;
        model.addAttribute("currentPage", currentPage);
        
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
}