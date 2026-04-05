package com.team.docrate.domain.doctor.entity;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;
import com.team.docrate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors", uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_doctor_info",
        columnNames = {"hospital_id", "department_id", "name"}
    )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private DoctorStatus status;


    public static Doctor from(DoctorRequest doctorRequest) {
        return Doctor.builder()
                .hospital(doctorRequest.getHospital())
                .department(doctorRequest.getDepartment())
                .name(doctorRequest.getName())
                .intro(doctorRequest.getIntro())
                .status(DoctorStatus.ACTIVE)
                .build();
    }
}
