package com.team.docrate.domain.doctor.dto;

import com.team.docrate.domain.doctor.entity.Doctor;

public class DoctorDetailResponse {

    private final Long doctorId;
    private final String doctorName;
    private final String hospitalName;
    private final String departmentName;
    private final Double averageRating;
    private final String intro;
    private final Double kindnessRating;
    private final Double explanationRating;
    private final Double waitingRating;
    private final Double revisitRating;

    public DoctorDetailResponse(Long doctorId,
                                String doctorName,
                                String hospitalName,
                                String departmentName,
                                Double averageRating,
                                String intro,
                                Double kindnessRating,
                                Double explanationRating,
                                Double waitingRating,
                                Double revisitRating) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.hospitalName = hospitalName;
        this.departmentName = departmentName;
        this.averageRating = averageRating;
        this.intro = intro;
        this.kindnessRating = kindnessRating;
        this.explanationRating = explanationRating;
        this.waitingRating = waitingRating;
        this.revisitRating = revisitRating;
    }

    public static DoctorDetailResponse from(Doctor doctor) {
        return new DoctorDetailResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getHospitalName(),
                doctor.getDepartmentName(),
                4.6,
                doctor.getIntro(),
                4.8,
                4.7,
                4.1,
                4.5
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

    public String getIntro() {
        return intro;
    }

    public Double getKindnessRating() {
        return kindnessRating;
    }

    public Double getExplanationRating() {
        return explanationRating;
    }

    public Double getWaitingRating() {
        return waitingRating;
    }

    public Double getRevisitRating() {
        return revisitRating;
    }
}