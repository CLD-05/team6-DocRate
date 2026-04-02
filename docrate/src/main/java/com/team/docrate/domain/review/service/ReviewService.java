package com.team.docrate.domain.review.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    
    // 작성자 본인인지 바로 검증하며 조회
    @Transactional(readOnly = true)
    public Review getReviewForEdit(Long reviewId, Long userId) {
        return reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new BusinessException("리뷰를 수정할 권한이 없거나 존재하지 않는 리뷰입니다."));
    }

    // 수정 후 리다이렉트를 위해 의사 ID가 필요한 경우 사용
    @Transactional(readOnly = true)
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException("리뷰를 찾을 수 없습니다."));
    }

    // 리뷰 목록(페이징 처리)
    @Transactional(readOnly = true)
    public Page<Review> listReviews(Long doctorId, int page) {
        // 1. createdAt이 같을 경우, id가 큰 순서대로 정렬하도록 기준 추가
        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        
        // 2. PageRequest 생성
        Pageable pageable = PageRequest.of(page, 5, sort);
        
        return reviewRepository.findPageByDoctorId(doctorId, pageable);
    }
    
    // 평균 별점 계산(전체 리스트 사용)
    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long doctorId) {
    	List<Review> reviews = reviewRepository.findAllByDoctorId(doctorId);
    	if (reviews.isEmpty()) return 0.0; // 리뷰가 없으면 0.0 반환
    	BigDecimal sum = reviews.stream()
    	        .map(Review::getRating)
    	        .reduce(BigDecimal.ZERO, BigDecimal::add);

    	    BigDecimal avg = sum.divide(
    	        BigDecimal.valueOf(reviews.size()),
    	        1,
    	        RoundingMode.HALF_UP
    	    );

    	    return avg.doubleValue();
    }
    
    // 리뷰 등록
    @Transactional
    public void createReview(Long doctorId, Long userId, ReviewCreateRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException("의사를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
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
    
    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, Long userId, ReviewCreateRequest request) {
        // 1. 해당 리뷰가 있고, 작성자가 본인인지 확인
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new BusinessException("리뷰를 수정할 권한이 없거나 존재하지 않습니다."));

        // 2. 값 변경 (Dirty Checking 활용)
        review.edit(
            request.getRating(),
            request.getBedsideManner(),
            request.getExplanation(),
            request.getWaitTime(),
            request.getRevisitIntention(),
            request.getContent()
        );
    }
    
    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new BusinessException("리뷰를 삭제할 권한이 없거나 존재하지 않습니다."));
                
        reviewRepository.delete(review);
    }
}