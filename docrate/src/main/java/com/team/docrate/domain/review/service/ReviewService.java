package com.team.docrate.domain.review.service;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.entity.Review;
import com.team.docrate.domain.review.repository.ReviewRepository;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;
import com.team.docrate.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

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
}