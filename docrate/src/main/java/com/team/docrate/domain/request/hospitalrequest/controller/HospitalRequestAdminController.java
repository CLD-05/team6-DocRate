package com.team.docrate.domain.request.hospitalrequest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.docrate.domain.request.doctorrequest.controller.DoctorRequestAdminController.RejectRequestDto;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestResponseDto;
import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/hospital-requests")
@RequiredArgsConstructor
public class HospitalRequestAdminController {

    private final HospitalRequestService hospitalRequestService;

    // 1. 목록 조회 기능 추가 (필요하시다면)
    @GetMapping
    public ResponseEntity<List<HospitalRequestResponseDto>> getAllRequests() {
        List<HospitalRequestResponseDto> responses = hospitalRequestService.getAllRequests();
        return ResponseEntity.ok(responses);
    }

    // 2. 승인 (기존 로직 유지)
    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable("id") Long id) {
        try {
            hospitalRequestService.approveRequest(id);
            return ResponseEntity.ok("승인 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 반려 (기본 사유 포함 가능하도록 확장)
    @PostMapping("/{id}/reject")
    public ResponseEntity<String> reject(
            @PathVariable("id") Long id,
            @RequestBody(required = false) RejectRequestDto rejectRequestDto
    ) {
        try {
            String reason = (rejectRequestDto != null && rejectRequestDto.getReason() != null
                    && !rejectRequestDto.getReason().trim().isEmpty())
                    ? rejectRequestDto.getReason().trim()
                    : "관리자 반려";

            hospitalRequestService.rejectRequest(id, reason);
            return ResponseEntity.ok("반려 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}