package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorDto;
import com.team.docrate.domain.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService = new DoctorService();

    // 의사 목록 (필터 가능)
    @GetMapping
    public List<DoctorDto> getDoctors(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) Long departmentId
    ) {
        return doctorService.getDoctors(hospitalId, departmentId);
    }

    // 의사 상세 조회
    @GetMapping("/{doctorId}")
    public DoctorDto getDoctorDetail(@PathVariable Long doctorId) {
        return doctorService.getDoctorDetail(doctorId);
    }

    // 병원별 의사 조회
    @GetMapping("/hospital/{hospitalId}")
    public List<DoctorDto> getDoctorsByHospital(@PathVariable Long hospitalId) {
        return doctorService.getDoctorsByHospital(hospitalId);
    }

    // 진료과별 의사 조회
    @GetMapping("/department/{departmentId}")
    public List<DoctorDto> getDoctorsByDepartment(@PathVariable Long departmentId) {
        return doctorService.getDoctorsByDepartment(departmentId);
    }
}