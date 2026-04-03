package com.team.docrate.domain.request.doctorrequest.controller;

import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestResponseDto;
import com.team.docrate.domain.request.doctorrequest.service.DoctorRequestService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctor-requests")
@RequiredArgsConstructor
public class DoctorRequestAdminController {

    private final DoctorRequestService doctorRequestService;

    @GetMapping
    public ResponseEntity<List<DoctorRequestResponseDto>> getAllRequests() {
        List<DoctorRequestResponseDto> responses = doctorRequestService.getAllRequests();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable("id") Long id) {
        doctorRequestService.approveRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable("id") Long id,
            @RequestBody(required = false) RejectRequestDto rejectRequestDto
    ) {
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