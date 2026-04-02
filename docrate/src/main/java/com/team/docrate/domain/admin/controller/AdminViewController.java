package com.team.docrate.domain.admin.controller; // 이 줄이 정확히 있어야 합니다!

import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;
import com.team.docrate.domain.request.doctorrequest.service.DoctorRequestService;
import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // 추가
import org.springframework.data.domain.Sort; // 추가
import org.springframework.data.web.PageableDefault; // 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final DoctorRequestService doctorRequestService;
    private final HospitalRequestService hospitalRequestService;

    @GetMapping("/hospital-requests") // HTML에서 호출하는 주소
    public String hospitalRequestsPage(
            Model model,
            @PageableDefault(size = 10) Pageable pageable, // 기본값 설정 필수
            @RequestParam(required = false) String status) {

        // 서비스 호출 시 반드시 pageable을 넘겨야 함
        Page<HospitalRequest> requestPage = hospitalRequestService.getRequestsPage(status, pageable);

        if (requestPage == null) {
            // 서비스에서 null을 반환하면 에러가 나므로 빈 페이지라도 생성
            model.addAttribute("requests", Collections.emptyList());
        } else {
            model.addAttribute("requests", requestPage.getContent());
            model.addAttribute("page", requestPage);
        }
        model.addAttribute("currentStatus", status);
        
        return "admin/requests/hospital-list"; 
    }

    /**
     * 의사 등록 요청 목록 페이지
     * 주소: GET /admin/doctor-requests
     */
    @GetMapping("/doctor-requests")
    public String doctorRequestsPage(
            Model model,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, 
            @RequestParam(required = false) String status) {

        // 의사 전용 서비스 호출 (Pageable 전달)
        Page<DoctorRequest> requestPage = doctorRequestService.getRequestsPage(status, pageable);

        if (requestPage == null || requestPage.isEmpty()) {
            model.addAttribute("requests", Collections.emptyList());
            model.addAttribute("page", Page.empty()); // 페이징 버튼 에러 방지용 빈 페이지 객체
        } else {
            model.addAttribute("requests", requestPage.getContent());
            model.addAttribute("page", requestPage);
        }
        
        model.addAttribute("currentStatus", status);
        
        // 의사 목록 HTML 경로로 리턴
        return "admin/requests/doctor-list"; 
    }
}