package com.team.docrate.domain.request.hospitalrequest.service;

import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestResponseDto;
import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import com.team.docrate.domain.request.hospitalrequest.repository.HospitalRequestRepository;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalRequestService {

    private final HospitalRequestRepository hospitalRequestRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    @Transactional
    public void approveRequest(Long requestId) {
        HospitalRequest request = hospitalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (request.getStatus() != HospitalRequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        boolean exists = hospitalRepository.existsByNameAndAddress(
                request.getName(),
                request.getAddress()
        );

        if (exists) {
            throw new IllegalStateException("이미 동일한 병원이 등록되어 있습니다.");
        }

        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .category(request.getCategory())
                .status(HospitalStatus.ACTIVE)
                .build();

        Hospital savedHospital = hospitalRepository.save(hospital);
        request.approve(savedHospital.getId());
    }

    @Transactional
    public void rejectRequest(Long requestId, String reason) {
        HospitalRequest request = hospitalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (request.getStatus() != HospitalRequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        request.reject(reason);
    }

    @Transactional(readOnly = true)
    public List<HospitalRequestResponseDto> getAllRequests() {
        return hospitalRequestRepository.findAll().stream()
                .map(HospitalRequestResponseDto::new)
                .toList();
    }

    public Page<HospitalRequest> getRequestsPage(String status, Pageable pageable) {
        if (status != null && !status.trim().isEmpty()) {
            try {
                HospitalRequestStatus requestStatus = HospitalRequestStatus.valueOf(status.toUpperCase());
                return hospitalRequestRepository.findByStatus(requestStatus, pageable);
            } catch (IllegalArgumentException e) {
                return hospitalRequestRepository.findAll(pageable);
            }
        }
        return hospitalRequestRepository.findAll(pageable);
    }

    @Transactional
    public void saveRequest(HospitalRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        HospitalRequest request = HospitalRequest.builder()
                .name(dto.getName().trim())
                .address(dto.getAddress().trim())
                .phone(dto.getPhone() != null ? dto.getPhone().trim() : null)
                .category(dto.getCategory().trim())
                .status(HospitalRequestStatus.PENDING)
                .requester(user)
                .build();

        hospitalRequestRepository.save(request);
    }
}