package com.team.docrate.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewSummaryDto {
    private Double averageRating;
    private Double bedsideMannerAvg;
    private Double explanationAvg;
    private Double waitTimeAvg;
    private Double revisitIntentionScore;
    private Long reviewCount;
}