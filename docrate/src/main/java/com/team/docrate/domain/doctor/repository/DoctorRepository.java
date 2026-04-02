package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findByStatus(DoctorStatus status, Pageable pageable);

    Page<Doctor> findByHospitalIdAndStatus(Long hospitalId, DoctorStatus status, Pageable pageable);

    Page<Doctor> findByDepartmentIdAndStatus(Long departmentId, DoctorStatus status, Pageable pageable);

    Page<Doctor> findByHospitalIdAndDepartmentIdAndStatus(
            Long hospitalId, Long departmentId, DoctorStatus status, Pageable pageable
    );
}