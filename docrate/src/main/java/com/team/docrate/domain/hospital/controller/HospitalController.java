package com.team.docrate.domain.hospital.controller;

import com.team.docrate.domain.hospital.dto.HospitalListItemDto;
import com.team.docrate.domain.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/hospitals")
    public String hospitalList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<HospitalListItemDto> hospitalPage = hospitalService.getHospitalList(search, pageable);

        model.addAttribute("hospitalPage", hospitalPage);
        model.addAttribute("hospitalList", hospitalPage.getContent());
        model.addAttribute("search", search);

        return "hospitals/list";
    }
}
