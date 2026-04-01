package com.team.docrate.domain.hospital.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.service.HospitalService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/hospitals") // Base path for hospital related endpoints
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // 병원 목록 조회 - GET /hospitals
    // 카테고리 필터링, 페이징, 기본 정렬 설정 (@PageableDefault 사용)
    @GetMapping
    public String getHospitalList(
            Model model,
            @RequestParam(required = false, name = "category") String category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<HospitalResponse> hospitalPage = hospitalService.getHospitalList(category, pageable);

        model.addAttribute("hospitals", hospitalPage.getContent());
        model.addAttribute("pageInfo", hospitalPage);

        // 🔥 [추가] HTML에서 요구하는 빈 리스트들을 일단 넣어줍니다.
        model.addAttribute("categories", new java.util.ArrayList<>()); 
        model.addAttribute("regions", new java.util.ArrayList<>());

        if (category != null && !category.isEmpty()) {
            model.addAttribute("selectedCategory", category);
        }

        return "hospital/hospitals"; 
    }

    // GET /hospitals/{hospitalId} endpoint for detail view will be implemented here later.
    
 // 병원 상세 조회 - GET /hospitals/{hospitalId}
 // 병원 상세 조회
    @GetMapping("/{hospitalId}")
    public String getHospitalDetail(@PathVariable Long hospitalId, Model model) {
        HospitalResponse hospital = hospitalService.getHospitalById(hospitalId);
        model.addAttribute("hospital", hospital);
        
        // 의사 기능은 아직 구현 전일 테니 빈 리스트로 에러 방지
        model.addAttribute("doctors", new java.util.ArrayList<>()); 
        return "hospital/hospitalDetail";
    }

    // 등록 요청 폼 화면으로 이동
    @GetMapping("/request")
    public String showRequestForm() {
        return "hospital/requestForm";
    }
    
}
