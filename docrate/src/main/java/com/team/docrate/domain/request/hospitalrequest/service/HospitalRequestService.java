package com.team.docrate.domain.request.hospitalrequest.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import com.team.docrate.domain.request.hospitalrequest.repository.HospitalRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalRequestService {

    private final HospitalRequestRepository requestRepository;
    private final HospitalRepository hospitalRepository; // domain/hospital/repository

    public void approveRequest(Long requestId) {
        HospitalRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));

     // 1. 요청 상태를 APPROVED로 변경
        request.updateStatus(HospitalRequestStatus.APPROVED);

        // 2. 실제 병원(Hospital) 엔티티 생성 및 저장
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .category(request.getCategory())
                .status(HospitalStatus.ACTIVE) // 엔티티의 nullable=false 조건 충족
                .build();
        // 2. 요청 상태 변경
        hospitalRepository.save(hospital);
    }

    public void rejectRequest(Long requestId, String reason) {
        HospitalRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));
        
        request.reject(reason);
    }

	public Object findAllPendingRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<HospitalRequest> getAllRequests() {
		// TODO Auto-generated method stub
		return requestRepository.findAll();
	}


}
