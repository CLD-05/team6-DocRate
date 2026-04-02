package com.team.docrate.domain.doctor.dto;

import com.team.docrate.domain.doctor.entity.Doctor;

public class DoctorResponse {

    private final Long doctorId;
    private final String doctorName;
    private final String hospitalName;
    private final String departmentName;
    private final Double averageRating;

    public DoctorResponse(Long doctorId, String doctorName, String hospitalName, String departmentName, Double averageRating) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.hospitalName = hospitalName;
        this.departmentName = departmentName;
        this.averageRating = averageRating;
    }

    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getHospitalName(),
                doctor.getDepartmentName(),
                4.6
        );
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Double getAverageRating() {
        return averageRating;
    }
}