package com.team.docrate.domain.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.review.dto.ReviewCreateRequest;
import com.team.docrate.domain.review.dto.ReviewResponse;
import com.team.docrate.domain.review.dto.ReviewSummaryDto;
import com.team.docrate.domain.review.entity.Review;
import com.team.docrate.domain.review.repository.ReviewRepository;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의사를 찾을 수 없습니다. id=" + doctorId));
    }

    public List<ReviewResponse> getDoctorReviews(Long doctorId) {
        return reviewRepository.findAllByDoctorId(doctorId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewSummaryDto getDoctorReviewSummary(Long doctorId) {
        List<Review> reviews = reviewRepository.findAllByDoctorId(doctorId);

        if (reviews.isEmpty()) {
            return new ReviewSummaryDto(0.0, 0.0, 0.0, 0.0, 0.0, 0L);
        }

        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        double kindnessRating = reviews.stream()
                .mapToDouble(r -> r.getBedsideManner().doubleValue())
                .average()
                .orElse(0.0);

        double explanationRating = reviews.stream()
                .mapToDouble(r -> r.getExplanation().doubleValue())
                .average()
                .orElse(0.0);

        double waitingRating = reviews.stream()
                .mapToDouble(r -> r.getWaitTime().doubleValue())
                .average()
                .orElse(0.0);

        double revisitRating = reviews.stream()
                .mapToDouble(r -> r.getRevisitIntention() ? 5.0 : 0.0)
                .average()
                .orElse(0.0);

        return new ReviewSummaryDto(
                averageRating,
                kindnessRating,
                explanationRating,
                waitingRating,
                revisitRating,
                (long) reviews.size()
        );
    }

    @Transactional
    public void registerReview(Long doctorId, String loginId, ReviewCreateRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의사를 찾을 수 없습니다. id=" + doctorId));

        User user = userRepository.findByEmail(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. loginId=" + loginId));

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