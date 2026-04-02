package com.team.docrate.domain.doctor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String intro;

    @Column(name = "hospital_id", nullable = false)
    private String hospitalName;

    @Column(name = "department_id", nullable = false)
    private String departmentName;

    protected Doctor() {
    }

    public Doctor(String name, String intro, String hospitalName, String departmentName) {
        this.name = name;
        this.intro = intro;
        this.hospitalName = hospitalName;
        this.departmentName = departmentName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIntro() {
        return intro;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDepartmentName() {
        return departmentName;
    }
}