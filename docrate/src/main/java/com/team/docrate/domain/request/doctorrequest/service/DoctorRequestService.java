package com.team.docrate.domain.request.doctorrequest.service;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestResponseDto;
import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;
import com.team.docrate.domain.request.doctorrequest.enumtype.DoctorRequestStatus;
import com.team.docrate.domain.request.doctorrequest.repository.DoctorRequestRepository;



import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorRequestService {

    private final DoctorRequestRepository doctorRequestRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public void approveRequest(Long requestId) {
        // 1. 승인 대기 중인 요청 조회
        DoctorRequest request = doctorRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        if (request.getStatus() != DoctorRequestStatus.PENDING) {
        	throw new IllegalStateException("이미 처리된 요청입니다.");
        }
        
        boolean isDuplicate = doctorRepository.existsByHospitalAndDepartmentAndName(
                request.getHospital(), 
                request.getDepartment(), 
                request.getName()
        );

        if (isDuplicate) {
            throw new IllegalStateException("해당 병원에 이미 등록된 의사 정보입니다.");
        }
        

        // 2. 새로운 Doctor 엔티티 생성 및 저장 (시스템에 의사 등록)
        Doctor newDoctor = Doctor.builder()
                .hospital(request.getHospital())
                .department(request.getDepartment())
                .name(request.getName())
                .intro(request.getIntro())
                .status(DoctorStatus.ACTIVE) // 바로 활동 가능 상태로 등록
                .build();

        Doctor savedDoctor = doctorRepository.save(newDoctor);

        // 3. 요청 데이터에 승인 결과 업데이트 (어떤 의사 ID로 등록됐는지 기록)
        request.approve(savedDoctor.getId());
    }

    @Transactional
    public void rejectRequest(Long requestId, String reason) {
        DoctorRequest request = doctorRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        request.reject(reason);
    }
 	// 1. 대기 중인 요청 목록 조회 (PENDING 상태만)
    public List<DoctorRequestResponseDto> getPendingRequests() {
        return doctorRequestRepository.findAll().stream()
                .filter(request -> request.getStatus() == DoctorRequestStatus.PENDING)
                .map(DoctorRequestResponseDto::new)
                .toList();
    }
    // 2. 전체 요청 목록 조회 (APPROVED, REJECTED 포함)
    public List<DoctorRequestResponseDto> getAllRequests() {
        return doctorRequestRepository.findAll().stream()
                .map(DoctorRequestResponseDto::new)
                .toList();
    }

    public Page<DoctorRequest> getRequestsPage(String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            // String을 Enum으로 변환하는 과정이 필요할 수 있습니다.
            DoctorRequestStatus enumStatus = DoctorRequestStatus.valueOf(status.toUpperCase());
            return doctorRequestRepository.findByStatus(enumStatus, pageable);
        }
        return doctorRequestRepository.findAll(pageable);
    }

}