package com.team.docrate.domain.hospital.service;

import com.team.docrate.domain.hospital.dto.HospitalListItemDto;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public Page<HospitalListItemDto> getHospitalList(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return hospitalRepository.findByStatusAndNameContainingIgnoreCase(
                            HospitalStatus.ACTIVE,
                            search.trim(),
                            pageable
                    )
                    .map(HospitalListItemDto::from);
        }

        return hospitalRepository.findByStatus(HospitalStatus.ACTIVE, pageable)
                .map(HospitalListItemDto::from);
    }
}