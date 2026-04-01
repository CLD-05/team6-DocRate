package com.team.docrate.domain.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.entity.Review;
import com.team.docrate.domain.review.repository.ReviewRepository;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;
import com.team.docrate.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true) // 단순 조회이므로 readOnly 설정 추천
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("의사를 찾을 수 없습니다."));
    }

    @Transactional
    public void createReview(Long doctorId, ReviewCreateRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("의사를 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("회원을 찾을 수 없습니다."));

        Review review = Review.builder()
                .doctor(doctor)
                .user(user)
                .rating(request.getRating())
                .bedsideManner(request.getBedsideManner())
                .explanation(request.getExplanation())
                .waitTime(request.getWaitTime())
                .revisitIntention(request.getRevisitIntention())
                .content(request.getContent())
                .build();

        reviewRepository.save(review);
    }
    
    public List<Review> findAllByDoctorId(Long doctorId) {
        return reviewRepository.findAllByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    public Double calculateAverageRating(Long doctorId) {
        List<Review> reviews = reviewRepository.findAllByDoctorIdOrderByCreatedAtDesc(doctorId);
        if (reviews.isEmpty()) return 0.0;
        
        double sum = reviews.stream().mapToDouble(Review::getRating).sum();
        return Math.round((sum / reviews.size()) * 10.0) / 10.0; // 소수점 첫째자리까지
    }
}