package com.team.docrate.domain.request.doctorrequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;
import com.team.docrate.domain.request.doctorrequest.enumtype.DoctorRequestStatus;

@Repository
public interface DoctorRequestRepository extends JpaRepository<DoctorRequest, Long> {

	Page<DoctorRequest> findByStatus(DoctorRequestStatus enumStatus, Pageable pageable);

	
}
