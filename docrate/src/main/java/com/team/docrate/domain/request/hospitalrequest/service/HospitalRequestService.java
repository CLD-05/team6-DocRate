package com.team.docrate.domain.request.hospitalrequest.service;

import com.team.docrate.domain.hospital.repository.HospitalRepository;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestResponseDto;
import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.repository.HospitalRequestRepository;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import com.team.docrate.domain.user.entity.User; // 유저 엔티티 임포트
import lombok.RequiredArgsConstructor;



import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@Transactional
@RequiredArgsConstructor
public class HospitalRequestService {

	private final HospitalRequestRepository hospitalRequestRepository;
    private final HospitalRepository hospitalRepository;

    @Transactional // ⭐ 반드시 붙어 있어야 DB에 반영됩니다!
    public void approveRequest(Long requestId) {
        HospitalRequest request = hospitalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (request.getStatus() != HospitalRequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 1. 상태 변경
        request.approve(); 

        // 2. 실제 병원(Hospital) 엔티티 생성 및 저장 로직이 있다면 여기서 수행
        // Hospital hospital = Hospital.builder()...build();
        // hospitalRepository.save(hospital);
    }

    @Transactional
    public void rejectRequest(Long requestId, String reason) {
        HospitalRequest request = hospitalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
        
        // 엔티티에 사유를 저장하는 메서드가 있다면 활용하세요.
        request.reject(); 
        }
        // request.setRejectionReason(reason); // 필드가 있다면 추가

	public Object findAllPendingRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(readOnly = true)
	public List<HospitalRequestResponseDto> getAllRequests() {
	    return hospitalRequestRepository.findAll().stream()
	            .map(HospitalRequestResponseDto::new) // 생성자 참조 사용
	            .toList(); // Java 16 이상 기준, 이하는 .collect(Collectors.toList())
	}

	public void rejectRequest(Long id) {
		// TODO Auto-generated method stub
		this.rejectRequest(id, "관리자 반려");
	}

	public Page<HospitalRequest> getRequestsPage(String status, Pageable pageable) {
        // 1. status 파라미터가 넘어왔는지 확인
        if (status != null && !status.trim().isEmpty()) {
            try {
                // 2. 문자열을 엔티티에서 사용하는 HospitalRequestStatus Enum으로 변환
                HospitalRequestStatus requestStatus = HospitalRequestStatus.valueOf(status.toUpperCase());
                return hospitalRequestRepository.findByStatus(requestStatus, pageable);
            } catch (IllegalArgumentException e) {
                // 3. 잘못된 상태 값이 올 경우(예: 주소창에 직접 타이핑) 무시하고 전체 조회
                return hospitalRequestRepository.findAll(pageable);
            }
        }
        // 4. 필터가 없으면 전체 조회
        return hospitalRequestRepository.findAll(pageable);
    }

    @Transactional
    public void saveRequest(HospitalRequestDto dto, User user) {
        HospitalRequest request = HospitalRequest.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .category(dto.getCategory())
                .status(HospitalRequestStatus.PENDING)
                .requester(user)
                .build();

        // 여기서 에러가 났던 건데, 위에서 extends JpaRepository를 하면 해결됩니다!
        hospitalRequestRepository.save(request); 
    }

}