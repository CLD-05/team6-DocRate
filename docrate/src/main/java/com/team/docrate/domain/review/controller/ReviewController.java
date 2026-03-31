package com.team.docrate.domain.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    
    @GetMapping("/doctors/{doctorId}/reviews/new")
    public String showReviewForm(@PathVariable Long doctorId, Model model) {
        // 폼에서 사용할 빈 객체를 넘겨주어야 타임리프(Thymeleaf) 에러가 나지 않습니다.
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());
        model.addAttribute("doctorId", doctorId);
        
        return "reviews/form"; // src/main/resources/templates/reviews/form.html
    }

    @PostMapping("/doctors/{doctorId}/reviews")
    public String createReview(
    		@PathVariable Long doctorId,
    		@Valid @ModelAttribute("reviewCreateRequest") ReviewCreateRequest request,
    		BindingResult bindingResult,
    		Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("doctorId", doctorId);
            return "review/form";
        }

        reviewService.createReview(doctorId, request);
        return "redirect:/doctors/" + doctorId;
    }
}