package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.doctor.entity.Doctor;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 병원 기준 조회
    List<Doctor> findByHospitalId(Long hospitalId);

    // 진료과 기준 조회
    List<Doctor> findByDepartmentId(Long departmentId);

    // 상세 조회 (fetch join)
    @Query("select d from Doctor d " +
           "join fetch d.hospital " +
           "join fetch d.department " +
           "where d.id = :id")
    Optional<Doctor> findDetailById(@Param("id") Long id);

    // 병원 + 진료과 동시 필터
    @Query("select d from Doctor d " +
           "join fetch d.hospital " +
           "join fetch d.department " +
           "where (:hospitalId is null or d.hospital.id = :hospitalId) " +
           "and (:departmentId is null or d.department.id = :departmentId)")
    List<Doctor> findByCondition(
            @Param("hospitalId") Long hospitalId,
            @Param("departmentId") Long departmentId
    );
}