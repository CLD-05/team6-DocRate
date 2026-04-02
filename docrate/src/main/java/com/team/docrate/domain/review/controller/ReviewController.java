package com.team.docrate.domain.review.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/doctors/{doctorId}/reviews/new")
    public String renderReviewForm(@PathVariable Long doctorId, Model model) {
        Doctor doctor = reviewService.getDoctorById(doctorId);
        model.addAttribute("doctor", doctor);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());
        model.addAttribute("doctorId", doctorId);

        return "review/form";
    }

    @PostMapping("/doctors/{doctorId}/reviews")
    public String registerReview(
            @PathVariable Long doctorId,
            @Valid @ModelAttribute("reviewCreateRequest") ReviewCreateRequest request,
            BindingResult bindingResult,
            Model model,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            Doctor doctor = reviewService.getDoctorById(doctorId);
            model.addAttribute("doctor", doctor);
            model.addAttribute("doctorId", doctorId);
            return "review/form";
        }

        String loginId = authentication.getName();
        reviewService.registerReview(doctorId, loginId, request);

        return "redirect:/doctors/" + doctorId + "/reviews";
    }
}