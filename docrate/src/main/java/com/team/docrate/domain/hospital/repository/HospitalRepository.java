package com.team.docrate.domain.hospital.repository;

import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Page<Hospital> findByStatus(HospitalStatus status, Pageable pageable);

    Page<Hospital> findByStatusAndNameContainingIgnoreCase(
            HospitalStatus status,
            String name,
            Pageable pageable
    );
}