package com.team.docrate.domain.admin.controller;

import com.team.docrate.domain.request.doctorrequest.service.DoctorRequestService;
import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller; // 주의: @Controller 사용
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 화면을 반환할 때는 @Controller를 써야 합니다.
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

	private final DoctorRequestService doctorRequestService;
    private final HospitalRequestService hospitalRequestService;

    @GetMapping("/requests/hospitals")
    public String hospitalRequestsPage(Model model) {
    	List<HospitalRequest> requests = hospitalRequestService.getAllRequests();
        
        // 콘솔창에 데이터 개수가 찍히는지 확인하세요!
        System.out.println(">>> 가져온 요청 데이터 개수: " + requests.size()); 
        
        model.addAttribute("requests", requests);
        return "admin/requests/hospitals"; 
    }
    
    @GetMapping("/requests/doctors") // 브라우저 주소: /admin/requests/doctors
    public String doctorRequestsPage(Model model) {
        // 의사 등록 요청 목록을 가져와서 모델에 담기
        model.addAttribute("requests", doctorRequestService.getAllRequests());
        
        // templates/admin/requests/doctors.html 반환
        return "admin/requests/doctor-list"; 
    }
    
}