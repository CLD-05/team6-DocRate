package com.team.docrate.domain.request.doctorrequest.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorRequestResponseDto {

    private Long id;

    private Long requesterUserId;
    private String requesterEmail;

    private Long hospitalId;
    private String hospitalName;

    private Long departmentId;
    private String departmentName;

    private String name;
    private String intro;
    private String status;

    private Long approvedDoctorId;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}