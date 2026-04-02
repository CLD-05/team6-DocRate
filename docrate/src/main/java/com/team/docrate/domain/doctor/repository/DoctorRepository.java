package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.hospital.entity.Hospital;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


import com.team.docrate.domain.doctor.enumtype.DoctorStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	boolean existsByHospitalAndDepartmentAndName(Hospital hospital, Department department, String name);
	
    List<Doctor> findByHospitalId(Long hospitalId);

	Optional<Doctor> findById(Long doctorId);
	
	 @EntityGraph(attributePaths = {"hospital", "department"})
	    Page<Doctor> findByStatus(DoctorStatus status, Pageable pageable);

	    @EntityGraph(attributePaths = {"hospital", "department"})
	    Page<Doctor> findByStatusAndNameContainingIgnoreCase(
	            DoctorStatus status,
	            String name,
	            Pageable pageable
	            );
	
	
}

