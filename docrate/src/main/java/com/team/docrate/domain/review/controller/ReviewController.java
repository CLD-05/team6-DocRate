package com.team.docrate.domain.review.controller;

import jakarta.servlet.http.HttpServletRequest;
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
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request,
            Model model) {

        Doctor doctor = reviewService.getDoctorById(doctorId);

        int jpaPage = (page > 0) ? page - 1 : 0;
        Page<Review> reviewPage = reviewService.listReviews(doctorId, jpaPage);

        Double averageRating = reviewService.calculateAverageRating(doctorId);

        model.addAttribute("doctor", doctor);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("currentUrl", request.getRequestURI());

        if (email != null) {
            User loginUser = userService.findByEmail(email).orElse(null);
            model.addAttribute("loginUser", loginUser);
        }

        return "review/list";
    }

    @GetMapping("/doctors/{doctorId}/reviews/new")
    public String newForm(
            @PathVariable Long doctorId,
            @AuthenticationPrincipal String email,
            Model model) {

        Doctor doctor = reviewService.getDoctorById(doctorId);

        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));

        model.addAttribute("doctor", doctor);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());

        return "review/form";
    }

    @PostMapping("/doctors/{doctorId}/reviews")
    public String create(
            @PathVariable Long doctorId,
            @AuthenticationPrincipal String email,
            @Valid @ModelAttribute("reviewCreateRequest") ReviewCreateRequest request,
            BindingResult bindingResult,
            Model model) {

        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));

        if (bindingResult.hasErrors()) {
            Doctor doctor = reviewService.getDoctorById(doctorId);
            model.addAttribute("doctor", doctor);
            model.addAttribute("loginUser", loginUser);
            return "review/form";
        }

        reviewService.createReview(doctorId, loginUser.getId(), request);

        return "redirect:/doctors/" + doctorId + "/reviews";
    }

    @GetMapping("/reviews/{reviewId}/edit")
    public String editForm(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal String email,
            Model model) {

        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 유효하지 않습니다."));

        Review review = reviewService.getReviewForEdit(reviewId, loginUser.getId());

        model.addAttribute("review", review);
        model.addAttribute("doctor", review.getDoctor());
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("reviewCreateRequest", new ReviewCreateRequest());

        return "review/form";
    }

    @PostMapping("/reviews/{reviewId}/edit")
    public String update(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal String email,
            @Valid @ModelAttribute("reviewCreateRequest") ReviewCreateRequest request,
            BindingResult bindingResult,
            Model model) {

        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 유효하지 않습니다."));

        Review review = reviewService.getReviewById(reviewId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("review", review);
            model.addAttribute("doctor", review.getDoctor());
            model.addAttribute("loginUser", loginUser);
            return "review/form";
        }

        reviewService.updateReview(reviewId, loginUser.getId(), request);

        return "redirect:/doctors/" + review.getDoctor().getId() + "/reviews";
    }

    @PostMapping("/reviews/{reviewId}/delete")
    public String delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal String email) {

        User loginUser = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 유효하지 않습니다."));

        Long doctorId = reviewService.deleteReviewAndReturnDoctorId(reviewId, loginUser.getId());

        return "redirect:/doctors/" + doctorId + "/reviews";
    }
}