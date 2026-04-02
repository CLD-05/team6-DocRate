package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorListItemDto;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public Page<DoctorListItemDto> getDoctorList(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return doctorRepository.findByStatusAndNameContainingIgnoreCase(
                            DoctorStatus.ACTIVE,
                            search.trim(),
                            pageable
                    )
                    .map(DoctorListItemDto::from);
        }

        return doctorRepository.findByStatus(DoctorStatus.ACTIVE, pageable)
                .map(DoctorListItemDto::from);
    }
}
