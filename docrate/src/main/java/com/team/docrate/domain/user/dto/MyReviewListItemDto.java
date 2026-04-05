package com.team.docrate.domain.user.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyReviewListItemDto {

    private Long reviewId;
    private Long doctorId;
    private String doctorName;
    private String hospitalName;
    private BigDecimal rating;
    private String contentPreview;
    private String createdAt;
}
