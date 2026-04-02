package com.team.docrate.domain.doctor.dto;

import com.team.docrate.domain.doctor.entity.Doctor;
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

    public static DoctorListItemDto from(Doctor doctor) {
        return DoctorListItemDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .hospitalName(
                    doctor.getHospital() != null ? doctor.getHospital().getName() : "병원 정보 없음"
                )
                .departmentName(
                    doctor.getDepartment() != null ? doctor.getDepartment().getName() : "진료과 없음"
                )
                .intro(doctor.getIntro())
                .build();
    }
}
