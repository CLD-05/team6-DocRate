package com.team.docrate.domain.doctor.repository;

public interface DoctorDetailProjection {

    Long getDoctorId();
    String getDoctorName();
    String getHospitalName();
    String getDepartmentName();
    String getIntro();
    String getStatus();
}