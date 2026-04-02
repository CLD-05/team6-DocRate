package com.team.docrate.domain.request.doctorrequest.service;

import com.team.docrate.domain.department.entity.Department;
import com.team.docrate.domain.department.repository.DepartmentRepository;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestCreateRequestDto;
import com.team.docrate.domain.request.doctorrequest.dto.DoctorRequestResponseDto;
import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;
import com.team.docrate.domain.request.doctorrequest.enumtype.DoctorRequestStatus;
import com.team.docrate.domain.request.doctorrequest.repository.DoctorRequestRepository;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorRequestService {

    private final DoctorRequestRepository doctorRequestRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    /**
     * 사용자 의사 추가 요청 등록
     */
    @Transactional
    public void createDoctorRequest(String email, DoctorRequestCreateRequestDto requestDto) {
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Hospital hospital = hospitalRepository.findById(requestDto.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        Department department = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("진료과를 찾을 수 없습니다."));

        DoctorRequest doctorRequest = DoctorRequest.builder()
                .requester(requester)
                .hospital(hospital)
                .department(department)
                .name(requestDto.getName())
                .intro(requestDto.getIntro())
                .status(DoctorRequestStatus.PENDING)
                .build();

        doctorRequestRepository.save(doctorRequest);
    }

    /**
     * 관리자 요청 목록 조회 (전체)
     */
    public List<DoctorRequestResponseDto> getAllRequests() {
        return doctorRequestRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * 관리자 요청 목록 페이징 조회
     */
    public Page<DoctorRequest> getRequestsPage(String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return doctorRequestRepository.findAll(pageable);
        }

        DoctorRequestStatus requestStatus;
        try {
            requestStatus = DoctorRequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 요청 상태입니다: " + status);
        }

        return doctorRequestRepository.findByStatus(requestStatus, pageable);
    }

    /**
     * 관리자 요청 승인
     */
    @Transactional
    public void approveRequest(Long id) {
        DoctorRequest doctorRequest = doctorRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의사 요청을 찾을 수 없습니다."));

        Doctor doctor = Doctor.builder()
                .hospital(doctorRequest.getHospital())
                .department(doctorRequest.getDepartment())
                .name(doctorRequest.getName())
                .intro(doctorRequest.getIntro())
                .status(DoctorStatus.ACTIVE)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        doctorRequest.approve(savedDoctor.getId());
    }

    /**
     * 관리자 요청 반려
     */
    @Transactional
    public void rejectRequest(Long id, String reason) {
        DoctorRequest doctorRequest = doctorRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("의사 요청을 찾을 수 없습니다."));

        doctorRequest.reject(reason);
    }

    private DoctorRequestResponseDto toResponseDto(DoctorRequest doctorRequest) {
        return DoctorRequestResponseDto.builder()
                .id(doctorRequest.getId())
                .requesterUserId(doctorRequest.getRequester().getId())
                .requesterEmail(doctorRequest.getRequester().getEmail())
                .hospitalId(doctorRequest.getHospital().getId())
                .hospitalName(doctorRequest.getHospital().getName())
                .departmentId(doctorRequest.getDepartment().getId())
                .departmentName(doctorRequest.getDepartment().getName())
                .name(doctorRequest.getName())
                .intro(doctorRequest.getIntro())
                .status(doctorRequest.getStatus().name())
                .approvedDoctorId(doctorRequest.getApprovedDoctorId())
                .rejectionReason(doctorRequest.getRejectionReason())
                .approvedAt(doctorRequest.getApprovedAt())
                .createdAt(doctorRequest.getCreatedAt())
                .build();
    }
}