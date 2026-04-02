package com.team.docrate.domain.doctor.repository;

import com.team.docrate.domain.doctor.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findByNameContainingIgnoreCase(String search, Pageable pageable);
}