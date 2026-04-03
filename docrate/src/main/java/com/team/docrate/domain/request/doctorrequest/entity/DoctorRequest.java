package com.team.docrate.domain.request.doctorrequest.entity;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.request.doctorrequest.enumtype.DoctorRequestStatus;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DoctorRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "requester_user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @JoinColumn(name = "hospital_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;

    @JoinColumn(name = "department_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String intro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoctorRequestStatus status;

    private Long approvedDoctorId;

    private String rejectionReason;

    private LocalDateTime approvedAt;

    public void approve(Long doctorId) {
        this.status = DoctorRequestStatus.APPROVED;
        this.approvedDoctorId = doctorId;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.status = DoctorRequestStatus.REJECTED;
        this.rejectionReason = reason;
        this.approvedAt = LocalDateTime.now();
    }
}