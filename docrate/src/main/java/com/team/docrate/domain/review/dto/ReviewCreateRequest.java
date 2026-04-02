package com.team.docrate.domain.review.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {

    private Long doctorId;

    private Long userId;

    @Min(1)
    @Max(5)
    private BigDecimal rating;

    @Min(1)
    @Max(5)
    private Integer bedsideManner;

    @Min(1)
    @Max(5)
    private Integer explanation;

    @Min(1)
    @Max(5)
    private Integer waitTime;

    private Boolean revisitIntention;

    @NotBlank
    private String content;
}
