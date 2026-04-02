package com.team.docrate.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyReviewListItemDto {

    private Long reviewId;
    private String doctorName;
    private String hospitalName;
    private Double rating;
    private String contentPreview;
    private String createdAt;
}
