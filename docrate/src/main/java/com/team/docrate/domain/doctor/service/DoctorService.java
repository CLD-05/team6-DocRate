package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorDetailDto;
import com.team.docrate.domain.doctor.dto.DoctorListItemDto;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.doctor.repository.DoctorRepository;

import com.team.docrate.domain.review.repository.ReviewRepository;

import com.team.docrate.domain.review.dto.ReviewSummaryDto;
import com.team.docrate.domain.review.service.ReviewService;

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
    private final ReviewService reviewService;

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

    public DoctorDetailDto getDoctorDetail(Long doctorId) {
        Doctor doctor = doctorRepository.findWithHospitalAndDepartmentById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의사를 찾을 수 없습니다. id=" + doctorId));

        ReviewSummaryDto summary = reviewService.getDoctorReviewSummary(doctorId);

        System.out.println("summary = " + summary.getAverageRating() + ", "
                + summary.getBedsideMannerAvg() + ", "
                + summary.getExplanationAvg() + ", "
                + summary.getWaitTimeAvg() + ", "
                + summary.getRevisitIntentionScore());

        return DoctorDetailDto.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .hospitalName(
                        doctor.getHospital() != null ? doctor.getHospital().getName() : "병원 정보 없음"
                )
                .departmentName(
                        doctor.getDepartment() != null ? doctor.getDepartment().getName() : "진료과 없음"
                )
                .intro(
                        doctor.getIntro() != null && !doctor.getIntro().isBlank()
                                ? doctor.getIntro()
                                : "등록된 소개가 없습니다."
                )
                .averageRating(summary.getAverageRating())
                .kindnessRating(summary.getBedsideMannerAvg())
                .explanationRating(summary.getExplanationAvg())
                .waitingRating(summary.getWaitTimeAvg())
                .revisitRating(summary.getRevisitIntentionScore())
                .reviewCount(summary.getReviewCount())
                .build();
    }
}