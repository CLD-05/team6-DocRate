package com.team.docrate.domain.request.doctorrequest.controller;

import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestResponseDto;
import com.team.docrate.domain.request.doctorrequest.service.DoctorRequestService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctor-requests") // 도메인 기준 경로
@RequiredArgsConstructor
public class DoctorRequestAdminController {

    private final DoctorRequestService doctorRequestService;



    @Controller
    public class DoctorRequestController {

        @GetMapping("/doctor-requests/new")
        public String doctorRequestForm() {
            return "requests/doctor-form";
        }
    }
    
    
    /**
     * 의사 등록 요청 목록 조회
     * GET /admin/doctor-requests
     */
    @GetMapping
    public ResponseEntity<List<DoctorRequestResponseDto>> getAllRequests() {
        List<DoctorRequestResponseDto> responses = doctorRequestService.getAllRequests();
        return ResponseEntity.ok(responses);
    }

    /**
     * 의사 등록 요청 승인
     * POST /admin/doctor-requests/{id}/approve
     */
    @PostMapping("/{id}/approve") // 정해진 기준: POST 방식, 경로 변수명 id
    public ResponseEntity<Void> approveRequest(@PathVariable("id") Long id) {
        doctorRequestService.approveRequest(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 의사 등록 요청 반려 (거절)
     * POST /admin/doctor-requests/{id}/reject
     */
    @PostMapping("/{id}/reject") // 정해진 기준: POST 방식
    public ResponseEntity<Void> rejectRequest(
            @PathVariable("id") Long id,
            @RequestBody(required = false) RejectRequestDto rejectRequestDto) {
        
        String reason = (rejectRequestDto != null) ? rejectRequestDto.getReason() : "관리자 반려";
        doctorRequestService.rejectRequest(id, reason);
        return ResponseEntity.ok().build();
    }

    @Getter
    @NoArgsConstructor
    public static class RejectRequestDto {
        private String reason;
    }
    
}