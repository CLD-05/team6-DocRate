package com.team.docrate.domain.request.doctorrequest.dto;

import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;
import com.team.docrate.domain.request.doctorrequest.enumtype.DoctorRequestStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DoctorRequestResponseDto {
    private Long id;
    private String requesterNickname; // 신청한 유저 닉네임
    private String hospitalName;      // 병원 이름
    private String departmentName;    // 진료과 이름
    private String doctorName;        // 의사 이름
    private String intro;             // 소개글
    private DoctorRequestStatus status; // PENDING, APPROVED, REJECTED
    private LocalDateTime createdAt;   // 신청일

    public DoctorRequestResponseDto(DoctorRequest request) {
        this.id = request.getId();
        this.requesterNickname = request.getRequester().getNickname(); // User 엔티티에 nickname이 있다고 가정
        this.hospitalName = request.getHospital().getName();           // Hospital 엔티티에 name이 있다고 가정
        this.departmentName = request.getDepartment().getName();       // Department 엔티티에 name이 있다고 가정
        this.doctorName = request.getName();
        this.intro = request.getIntro();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
    }
}