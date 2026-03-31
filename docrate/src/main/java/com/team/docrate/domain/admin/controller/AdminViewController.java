package com.team.docrate.domain.admin.controller;

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

    private final HospitalRequestService requestService;

    @GetMapping("/requests/hospitals")
    public String hospitalRequestsPage(Model model) {
    	List<HospitalRequest> requests = requestService.getAllRequests();
        
        // 콘솔창에 데이터 개수가 찍히는지 확인하세요!
        System.out.println(">>> 가져온 요청 데이터 개수: " + requests.size()); 
        
        model.addAttribute("requests", requests);
        return "admin/requests/hospitals"; 
    }
    
}