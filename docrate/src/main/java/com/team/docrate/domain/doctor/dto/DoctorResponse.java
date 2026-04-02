package com.team.docrate.domain.doctor.dto;

import com.team.docrate.domain.doctor.entity.Doctor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorResponse {

    private Long doctorId;
    private String doctorName;
    private Long hospitalId;
    private Long departmentId;

    public static DoctorResponse from(Doctor doctor) {
        return DoctorResponse.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .hospitalId(doctor.getHospitalId())
                .departmentId(doctor.getDepartmentId())
                .build();
    }
}