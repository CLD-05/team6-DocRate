package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/doctors")
    public String list(Model model) {
        List<DoctorResponse> doctors = doctorService.getDoctors(null, null);

        System.out.println("🔥 doctors size = " + doctors.size());

        model.addAttribute("doctors", doctors);
        model.addAttribute("totalCount", doctors.size());
        model.addAttribute("keyword", "");
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 1);

        return "doctor/list";
    }
}