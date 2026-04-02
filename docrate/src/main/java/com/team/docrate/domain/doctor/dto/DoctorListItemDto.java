package com.team.docrate.domain.doctor.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorListItemDto {

    private Long id;
    private String name;
    private String hospitalName;
    private String departmentName;
    private String intro;
    private Double averageRating;
}
