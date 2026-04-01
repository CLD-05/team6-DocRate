package com.team.docrate.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public void registerReview(Long doctorId, ReviewCreateRequest request) {
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
    
    // 리뷰 목록(페이징 처리)
    @Transactional(readOnly = true)
    public Page<Review> getReviewPageByDoctor(Long doctorId, int page) {
    	Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());
        return reviewRepository.findPageByDoctorId(doctorId, pageable);
    }

    // 평균 별점 계산(전체 리스트 사용)
    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long doctorId) {
        List<Review> reviews = reviewRepository.findAllByDoctorId(doctorId);
        if (reviews.isEmpty()) return 0.0; // 리뷰가 없으면 0.0 반환
        double sum = reviews.stream().mapToDouble(Review::getRating).sum(); // 모든 리뷰의 rating 점수를 합산
        return Math.round((sum / reviews.size()) * 10.0) / 10.0; // 합계를 리뷰 개수로 나누고 소수점 첫째 자리까지 반올림
    }
}