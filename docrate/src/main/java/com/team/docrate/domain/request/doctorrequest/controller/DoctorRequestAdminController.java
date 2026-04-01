package com.team.docrate.domain.request.doctorrequest.controller;

import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestResponseDto;
import com.team.docrate.domain.request.doctorrequest.service.DoctorRequestService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/doctor-requests")
@RequiredArgsConstructor
public class DoctorRequestAdminController {

    private final DoctorRequestService doctorRequestService;

    /**
     * 의사 등록 요청 승인
     * PATCH /api/admin/doctor-requests/{requestId}/approve
     */
    @PatchMapping("/api/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable Long requestId) {
        doctorRequestService.approveRequest(requestId);
        return ResponseEntity.ok().build();
    }

    /**
     * 의사 등록 요청 거절
     * PATCH /api/admin/doctor-requests/{requestId}/reject
     */
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody RejectRequestDto rejectRequestDto) {
        
        doctorRequestService.rejectRequest(requestId, rejectRequestDto.getReason());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<DoctorRequestResponseDto>> getPendingRequests() {
        List<DoctorRequestResponseDto> responses = doctorRequestService.getPendingRequests();
        
        // 명시적으로 List 타입을 담아 반환
        return ResponseEntity.ok(responses); 
    }

    // 거절 사유를 받기 위한 내부 DTO
    @lombok.Getter
    @lombok.NoArgsConstructor
    public static class RejectRequestDto {
        private String reason;
    }
}