package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DoctorViewController {

    private final DoctorService doctorService;

    @GetMapping("/doctors")
    public String list(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorResponse> doctorPage = doctorService.getDoctors(hospitalId, departmentId, pageable);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("totalCount", doctorPage.getTotalElements());
        model.addAttribute("currentPage", doctorPage.getNumber());
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("hospitalId", hospitalId);
        model.addAttribute("departmentId", departmentId);

        return "doctor/list";
    }
}