package com.team.docrate.domain.doctor.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorDetailDto {
    private Long id;
    private String name;
    private String hospitalName;
    private String departmentName;
    private String intro;

    private Double averageRating;
    private Double kindnessRating;
    private Double explanationRating;
    private Double waitingRating;
    private Double revisitRating;
    private Long reviewCount;
}