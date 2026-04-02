package com.team.docrate.domain.request.doctorrequest.controller;

import com.team.docrate.domain.department.repository.DepartmentRepository;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestCreateRequestDto;
import com.team.docrate.domain.request.doctorrequest.service.DoctorRequestService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class DoctorRequestController {

    private final DoctorRequestService doctorRequestService;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/doctor-requests/new")
    public String doctorRequestForm(Model model) {
        if (!model.containsAttribute("doctorRequestCreateRequestDto")) {
            model.addAttribute("doctorRequestCreateRequestDto", new DoctorRequestCreateRequestDto());
        }

        model.addAttribute("hospitalList", hospitalRepository.findAll());
        model.addAttribute("departmentList", departmentRepository.findAll());

        return "request/doctor-form";
    }

    @PostMapping("/doctor-requests")
    public String createDoctorRequest(
            @Valid @ModelAttribute("doctorRequestCreateRequestDto") DoctorRequestCreateRequestDto doctorRequestCreateRequestDto,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        System.out.println("=== submit start ===");
        System.out.println("principal = " + (principal != null ? principal.getName() : "null"));
        System.out.println("hospitalId = " + doctorRequestCreateRequestDto.getHospitalId());
        System.out.println("departmentId = " + doctorRequestCreateRequestDto.getDepartmentId());
        System.out.println("name = " + doctorRequestCreateRequestDto.getName());
        System.out.println("intro = " + doctorRequestCreateRequestDto.getIntro());
        System.out.println("errors = " + bindingResult.getAllErrors());

        if (bindingResult.hasErrors()) {
            model.addAttribute("hospitalList", hospitalRepository.findAll());
            model.addAttribute("departmentList", departmentRepository.findAll());
            return "request/doctor-form";
        }

        if (principal == null) {
            return "redirect:/login";
        }

        doctorRequestService.createDoctorRequest(principal.getName(), doctorRequestCreateRequestDto);
        System.out.println("=== saved ===");
        return "redirect:/mypage";
    }
}