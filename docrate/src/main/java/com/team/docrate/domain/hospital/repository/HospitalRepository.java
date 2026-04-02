package com.team.docrate.domain.hospital.repository;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByNameContaining(String keyword);

	List<Hospital> findAll();

	Optional<Hospital> findByName(String string);
}
