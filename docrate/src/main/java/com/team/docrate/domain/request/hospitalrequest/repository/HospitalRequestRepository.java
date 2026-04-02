package com.team.docrate.domain.request.hospitalrequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;

public interface HospitalRequestRepository extends JpaRepository<HospitalRequest, Long>{
	
	List<HospitalRequest> findAllByStatus(HospitalRequestStatus status);
	
	Optional<HospitalRequest> findById(Long requestId);

	Page<HospitalRequest> findByStatus(HospitalRequestStatus status, Pageable pageable);

	
}
