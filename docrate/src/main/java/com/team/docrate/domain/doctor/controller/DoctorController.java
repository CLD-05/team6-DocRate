package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    // 의사 목록 조회
    @GetMapping
    public List<DoctorResponse> getDoctors(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) Long departmentId
    ) {
        return doctorService.getDoctors(hospitalId, departmentId);
    }
}