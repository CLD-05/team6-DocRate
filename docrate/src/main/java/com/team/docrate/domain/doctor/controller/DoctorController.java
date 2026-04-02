package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorDetailResponse;
import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/doctors")
    public Page<DoctorResponse> getDoctors(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return doctorService.getDoctors(search, pageable);
    }

    @GetMapping("/doctors/{doctorId}")
    public DoctorDetailResponse getDoctorDetail(@PathVariable Long doctorId) {
        return doctorService.getDoctorDetail(doctorId);
    }
}