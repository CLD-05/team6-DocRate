package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorListItemDto;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.review.repository.ReviewRepository;
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
    private final ReviewRepository reviewRepository;

    public Page<DoctorListItemDto> getDoctorList(String search, Pageable pageable) {
        if (StringUtils.hasText(search)) {
            return doctorRepository.findByStatusAndNameContainingIgnoreCase(
                            DoctorStatus.ACTIVE,
                            search.trim(),
                            pageable
                    )
                    .map(this::toDoctorListItemDto);
        }

        return doctorRepository.findByStatus(DoctorStatus.ACTIVE, pageable)
                .map(this::toDoctorListItemDto);
    }

    private DoctorListItemDto toDoctorListItemDto(Doctor doctor) {
        Double averageRating = reviewRepository.findAverageRatingByDoctorId(doctor.getId());

        return DoctorListItemDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .hospitalName(
                        doctor.getHospital() != null ? doctor.getHospital().getName() : "병원 정보 없음"
                )
                .departmentName(
                        doctor.getDepartment() != null ? doctor.getDepartment().getName() : "진료과 정보 없음"
                )
                .intro(
                        doctor.getIntro() != null && !doctor.getIntro().isBlank()
                                ? doctor.getIntro()
                                : "소개 정보가 없습니다."
                )
                .averageRating(averageRating)
                .build();
    }
}
