package com.team.docrate.domain.doctor.controller;

import com.team.docrate.domain.doctor.dto.DoctorDetailDto;
import com.team.docrate.domain.doctor.dto.DoctorListItemDto;
import com.team.docrate.domain.doctor.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/doctors")
    public String doctorList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DoctorListItemDto> doctorPage = doctorService.getDoctorList(search, pageable);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("doctorList", doctorPage.getContent());
        model.addAttribute("search", search);

        return "doctors/list";
    }

    @GetMapping("/doctors/{doctorId}")
    public String getDoctorDetail(@PathVariable Long doctorId, Model model) {
        DoctorDetailDto doctor = doctorService.getDoctorDetail(doctorId);
        model.addAttribute("doctor", doctor);
        return "doctors/detail";
    }
}