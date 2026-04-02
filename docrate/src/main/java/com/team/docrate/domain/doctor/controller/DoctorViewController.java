package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorDetailResponse;
import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DoctorViewController {

    private final DoctorService doctorService;

    public DoctorViewController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/doctors")
    public String list(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model
    ) {
        Page<DoctorResponse> doctorPage =
                doctorService.getDoctors(search, PageRequest.of(page, size));

        int currentPage = doctorPage.getNumber();
        int totalPages = doctorPage.getTotalPages();

        int startPage = Math.max(0, currentPage - 2);
        int endPage = Math.min(totalPages - 1, currentPage + 2);

        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("totalCount", doctorPage.getTotalElements());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "doctor/list";
    }

    @GetMapping("/doctors/{doctorId}")
    public String detail(@PathVariable Long doctorId, Model model) {
        DoctorDetailResponse doctor = doctorService.getDoctorDetail(doctorId);
        model.addAttribute("doctor", doctor);
        return "doctor/detail";
    }
}