package com.team.docrate.domain.request.hospitalrequest.dto;

import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HospitalRequestResponseDto {
    private Long id;
    private String requesterNickname; // 신청한 유저 닉네임
    private String name;              // 병원 이름
    private String address;           // 병원 주소
    private String category;          // 카테고리 (진료과목 등)
    private HospitalRequestStatus status; // PENDING, APPROVED, REJECTED
    private LocalDateTime createdAt;   // 신청일

    public HospitalRequestResponseDto(HospitalRequest request) {
        this.id = request.getId();
        // DoctorRequest DTO 기준을 따라 getRequester().getNickname() 사용
        this.requesterNickname = request.getRequester().getNickname();
        this.name = request.getName();
        this.address = request.getAddress();
        this.category = request.getCategory();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
    }
}