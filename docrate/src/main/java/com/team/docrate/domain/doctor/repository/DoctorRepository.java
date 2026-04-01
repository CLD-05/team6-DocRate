package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 전체 조회 + 필터
    @Query("select d from Doctor d " +
           "join fetch d.hospital " +
           "join fetch d.department " +
           "where (:hospitalId is null or d.hospital.id = :hospitalId) " +
           "and (:departmentId is null or d.department.id = :departmentId)")
    List<Doctor> findDoctors(
            @Param("hospitalId") Long hospitalId,
            @Param("departmentId") Long departmentId
    );
}