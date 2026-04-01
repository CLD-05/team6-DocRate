package com.team.docrate.domain.review.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    
    @GetMapping("/doctors/{doctorId}/reviews/new")
    public String showReviewForm(@PathVariable Long doctorId, Model model) {
    	// 1. ReviewService를 통해 의사 정보를 가져옵니다.
    	Doctor doctor = reviewService.getDoctorById(doctorId);
    	
    	// 2. 모델에 담기
        // 폼에서 사용할 빈 객체를 넘겨주어야 타임리프(Thymeleaf) 에러가 나지 않습니다.
    	model.addAttribute("doctor", doctor);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());
        model.addAttribute("doctorId", doctorId);
        
        return "review/form";
    }

    @PostMapping("/doctors/{doctorId}/reviews")
    public String createReview(
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

        reviewService.createReview(doctorId, request);
        return "redirect:/doctors/" + doctorId + "/reviews";
    }
    
 // 리뷰 목록을 보여주는 GET 메서드가 반드시 있어야 리다이렉트가 성공합니다.
    @GetMapping("/doctors/{doctorId}/reviews")
    public String listByDoctor(@PathVariable Long doctorId, Model model) {
    	// 1. 의사 기본 정보 가져오기
        Doctor doctor = reviewService.getDoctorById(doctorId);
        
        // 2. 해당 의사의 실제 리뷰 리스트 가져오기 (추가된 부분)
        List<Review> reviews = reviewService.findAllByDoctorId(doctorId);
        
        // 3. 평균 평점 계산해서 가져오기 (추가된 부분)
        Double averageRating = reviewService.calculateAverageRating(doctorId);

        // 4. 모델에 전부 담아서 HTML로 보내기
        model.addAttribute("doctor", doctor);
        model.addAttribute("reviews", reviews); // HTML의 th:each="review : ${reviews}"에 쓰임
        model.addAttribute("averageRating", averageRating);
        
        return "review/list"; 
    }
}