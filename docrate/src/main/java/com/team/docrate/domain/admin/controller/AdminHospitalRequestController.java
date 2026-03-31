package com.team.docrate.domain.admin.controller;

import com.team.docrate.domain.request.hospitalrequest.service.HospitalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin/hospitals-requests")
@RequiredArgsConstructor
public class AdminHospitalRequestController {

    private final HospitalRequestService requestService;

    // 승인 API
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<String> approve(@PathVariable Long requestId) {
        requestService.approveRequest(requestId);
        return ResponseEntity.ok("승인되었습니다.");
    }

    // 거절 API
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<String> reject(@PathVariable Long requestId) {
        requestService.rejectRequest(requestId, "관리자에 의해 거절됨"); // 임의로 넣음 "관리자에 의해 거절됨"
        return ResponseEntity.ok("거절되었습니다.");
    }
}