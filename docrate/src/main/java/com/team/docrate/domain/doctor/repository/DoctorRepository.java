package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.hospital.entity.Hospital;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	boolean existsByHospitalAndDepartmentAndName(Hospital hospital, Department department, String name);
	
    List<Doctor> findByHospitalId(Long hospitalId);

	Optional<Doctor> findById(Long doctorId);
	
	
}