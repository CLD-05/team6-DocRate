package com.team.docrate.domain.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.entity.Review;
import com.team.docrate.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    
    // 리뷰 작성 폼
    @GetMapping("/doctors/{doctorId}/reviews/new")
    public String renderReviewForm(@PathVariable Long doctorId, Model model) {
    	Doctor doctor = reviewService.getDoctorById(doctorId);
    	model.addAttribute("doctor", doctor);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());
        model.addAttribute("doctorId", doctorId);
        
        return "review/form";
    }
    
    // 리뷰 작성 처리
    @PostMapping("/doctors/{doctorId}/reviews")
    public String registerReview(
    		@PathVariable Long doctorId,
    		@Valid @ModelAttribute("reviewCreateRequest") ReviewCreateRequest request,
    		BindingResult bindingResult,
    		Model model) {
        if (bindingResult.hasErrors()) {
        	Doctor doctor = reviewService.getDoctorById(doctorId);
            model.addAttribute("doctor", doctor);
        	model.addAttribute("doctorId", doctorId);
            return "review/form";
        }

        reviewService.registerReview(doctorId, request);
        return "redirect:/doctors/" + doctorId + "/reviews";
    }
    
    // 리뷰 목록
    @GetMapping("/doctors/{doctorId}/reviews")
    public String renderReviewList(
    		@PathVariable Long doctorId,
    		@RequestParam(value = "page", defaultValue = "0") int page,
    		Model model) {
    	// 1. 의사 기본 정보 가져오기
        Doctor doctor = reviewService.getDoctorById(doctorId);
        
        // 2. 해당 의사의 실제 리뷰 리스트 가져오기
        Page<Review> reviewPage = reviewService.getReviewPageByDoctor(doctorId, page);
        
        // 3. 평균 평점 계산해서 가져오기
        Double averageRating = reviewService.calculateAverageRating(doctorId);

        // 4. 모델에 전부 담아서 HTML로 보내기
        model.addAttribute("doctor", doctor);
        model.addAttribute("reviews", reviewPage.getContent()); // 실제 리뷰 리스트
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("averageRating", averageRating);
        
        return "review/list"; 
    }
}