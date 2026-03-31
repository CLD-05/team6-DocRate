package com.team.docrate.domain.review.controller;

import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public String createReview(@Valid ReviewCreateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "review/create";
        }

        reviewService.createReview(request);
        return "redirect:/hospitals";
    }
}