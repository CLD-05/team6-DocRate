package com.team.docrate.domain.request.hospitalrequest.entity;

import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospital_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HospitalRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "requester_user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String phone;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HospitalRequestStatus status;

    private Long approvedHospitalId;

    private String rejectionReason;

    private LocalDateTime approvedAt;

        public void approve() {
        this.status = HospitalRequestStatus.APPROVED; // 필드에 값을 넣어야 함
    }

    public void reject() {
        this.status = HospitalRequestStatus.REJECTED; // 필드에 값을 넣어야 함
    }

	public void updateStatus(HospitalRequestStatus approved) {
		// TODO Auto-generated method stub	
	}
}