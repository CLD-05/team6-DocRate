package com.team.docrate.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {

    @NotNull
    @Min(0)
    @Max(5)
    private Double rating;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer bedsideManner;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer explanation;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer waitTime;

    @NotNull
    private Boolean revisitIntention;

    @NotBlank
    private String content;
}