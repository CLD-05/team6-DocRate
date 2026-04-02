package com.team.docrate.domain.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    
    @GetMapping("/doctors/{doctorId}/reviews")
    public String listByDoctor(
            @PathVariable Long doctorId,
            @AuthenticationPrincipal String email,
            // 기본값을 1로 설정 (사용자가 처음 들어오면 page=1)
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {
        
        Doctor doctor = reviewService.getDoctorById(doctorId);
        
        // [중요] 사용자가 1을 보내면 DB는 0번을 조회해야 함
        int jpaPage = (page > 0) ? page - 1 : 0;
        Page<Review> reviewPage = reviewService.listReviews(doctorId, jpaPage);
        
        Double averageRating = reviewService.calculateAverageRating(doctorId);
        
        model.addAttribute("doctor", doctor);
        model.addAttribute("reviews", reviewPage.getContent());
        
        // [중요] 사용자가 보고 있는 1, 2, 3... 숫자를 그대로 넘김
        model.addAttribute("currentPage", page); 
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("averageRating", averageRating);
        
        if (email != null) {
            User loginUser = userService.findByEmail(email).orElse(null);
            model.addAttribute("loginUser", loginUser);
        }
        
        return "review/list"; 
    }
    
    // 리뷰 작성 폼
    @GetMapping("/doctors/{doctorId}/reviews/new")
    public String newForm(@PathVariable Long doctorId, @AuthenticationPrincipal String email, Model model) {
    	Doctor doctor = reviewService.getDoctorById(doctorId);
    	
    	User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));
    	
    	model.addAttribute("doctor", doctor);
    	model.addAttribute("loginUser", loginUser);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());
                
        return "review/form";
    }
    
    // 리뷰 작성 처리
    @PostMapping("/doctors/{doctorId}/reviews")
    public String create(
            @PathVariable Long doctorId,
            @AuthenticationPrincipal String email, 
            @Valid @ModelAttribute("reviewCreateRequest") ReviewCreateRequest request,
            BindingResult bindingResult,
            Model model) {
    	
        if (bindingResult.hasErrors()) {
            Doctor doctor = reviewService.getDoctorById(doctorId);
            model.addAttribute("doctor", doctor);
            model.addAttribute("doctorId", doctorId);
            return "review/form";
        }

        // 1. 현재 로그인한 유저를 DB에서 찾습니다.
        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));

        // 2. 서비스 호출 시 찾은 유저의 ID(loginUser.getId())를 '두 번째' 인자로 넘깁니다.
        reviewService.createReview(doctorId, loginUser.getId(), request); 
        
        return "redirect:/doctors/" + doctorId + "/reviews";
    }
    
    
 // 리뷰 수정 폼
    @GetMapping("/reviews/{reviewId}/edit")
    public String editForm(@PathVariable Long reviewId, 
                          @AuthenticationPrincipal String email, 
                          Model model) {
        
        // userService.findByEmail이 Optional<User>를 반환하므로 .orElseThrow()를 사용합니다.
        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 유효하지 않습니다."));
        
        Long loginUserId = loginUser.getId();

        // 서비스에서 본인 확인 로직(review.userId == loginUserId)을 거쳐 데이터를 가져옵니다.
        Review review = reviewService.getReviewForEdit(reviewId, loginUserId);

        model.addAttribute("review", review);
        model.addAttribute("doctor", review.getDoctor());
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());
        
        return "review/form";
    }

    // 리뷰 수정 처리
    @PostMapping("/reviews/{reviewId}/edit")
    public String update(@PathVariable Long reviewId, 
                       @AuthenticationPrincipal String email,
                       @ModelAttribute ReviewCreateRequest request) {
        
        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 유효하지 않습니다."));
        
        reviewService.updateReview(reviewId, loginUser.getId(), request);
        
        Review review = reviewService.getReviewById(reviewId);
        return "redirect:/doctors/" + review.getDoctor().getId() + "/reviews";
    }

    // 리뷰 삭제 처리
    @PostMapping("/reviews/{reviewId}/delete")
    public String delete(@PathVariable Long reviewId, 
                         @AuthenticationPrincipal String email) {
        
        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 유효하지 않습니다."));
        
        reviewService.deleteReview(reviewId, loginUser.getId());
        
        return "redirect:/mypage";
    }
}