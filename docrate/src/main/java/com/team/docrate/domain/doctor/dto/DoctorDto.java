package com.team.docrate.domain.doctor.dto;

import com.team.docrate.domain.doctor.entity.Doctor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorDto {

    private Long doctorId;
    private String doctorName;
    private String intro;
    private String status;

    private Long hospitalId;
    private String hospitalName;

    private Long departmentId;
    private String departmentName;

    public static DoctorDto from(Doctor doctor) {
        return DoctorDto.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .intro(doctor.getIntro())
                .status(doctor.getStatus().name())
                .hospitalId(doctor.getHospital().getId())
                .hospitalName(doctor.getHospital().getName())
                .departmentId(doctor.getDepartment().getId())
                .departmentName(doctor.getDepartment().getName())
                .build();
    }

	private static Object builder() {
		// TODO Auto-generated method stub
		return null;
	}
}