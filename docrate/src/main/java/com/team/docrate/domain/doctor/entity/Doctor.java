package com.team.docrate.domain.doctor.entity;

import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String intro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoctorStatus status;
}