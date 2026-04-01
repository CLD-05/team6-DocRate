package com.team.docrate.domain.department.repository;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.hospital.entity.Hospital;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Optional<Department> findByName(String string);
}