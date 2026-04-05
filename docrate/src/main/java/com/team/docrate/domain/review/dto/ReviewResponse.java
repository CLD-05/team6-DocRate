package com.team.docrate.domain.review.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.team.docrate.domain.review.entity.Review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private String userName;
    private String doctorName;
    private BigDecimal rating;
//    상세 점수 추가
    private Integer bedsideManner;
    private Integer explanation;
    private Integer waitTime;
    private Boolean revisitIntention;
    private String content;
    private LocalDateTime createdAt;
    
 // Entity를 DTO로 바로 변환해주는 편의 메서드
    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userName(review.getUser().getNickname())
                .doctorName(review.getDoctor().getName())
                .rating(review.getRating())
                .bedsideManner(review.getBedsideManner())
                .explanation(review.getExplanation())
                .waitTime(review.getWaitTime())
                .revisitIntention(review.getRevisitIntention())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
