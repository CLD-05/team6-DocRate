package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByHospitalId(Long hospitalId);

	Optional<Doctor> findById(Long doctorId);
}

