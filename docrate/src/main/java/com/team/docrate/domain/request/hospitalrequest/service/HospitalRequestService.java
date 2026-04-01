package com.team.docrate.domain.request.hospitalrequest.service;

import com.team.docrate.domain.request.hospitalrequest.dto.HospitalRequestDto;
import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.repository.HospitalRequestRepository;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import com.team.docrate.domain.user.entity.User; // 유저 엔티티 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HospitalRequestService {

    private final HospitalRequestRepository hospitalRequestRepository;

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