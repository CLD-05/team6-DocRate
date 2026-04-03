package com.team.docrate.domain.hospital.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;
import com.team.docrate.domain.hospital.repository.HospitalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;

    // 검색어(search), 카테고리(category), 페이징 정보를 받아 병원 목록 조회
    public Page<HospitalResponse> getHospitalList(String search, String category, Pageable pageable) {
        Page<Hospital> hospitalPage;

        if (search != null && !search.isEmpty()) {
            // 검색어가 있는 경우
            hospitalPage = hospitalRepository.findByStatusAndSearch(HospitalStatus.ACTIVE, search, pageable);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리 필터가 있는 경우
            String processedCategory = category.toLowerCase().replaceAll("\\s+", "");
            hospitalPage = hospitalRepository.findActiveAndProcessedCategory(
                    HospitalStatus.ACTIVE,
                    processedCategory,
                    pageable
            );
        } else {
            // 필터가 없는 경우 전체 조회
            hospitalPage = hospitalRepository.findAll(pageable);
        }

        List<HospitalResponse> hospitalResponses = hospitalPage.getContent().stream()
                .map(HospitalResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(hospitalResponses, pageable, hospitalPage.getTotalElements());
    }

    // 기본 목록 조회
    public Page<HospitalResponse> getHospitalList(Pageable pageable) {
        return getHospitalList(null, null, pageable);
    }

    // 카테고리 필터링 조회
    public Page<HospitalResponse> getHospitalListByCategory(String category, Pageable pageable) {
        return getHospitalList(null, category, pageable);
    }

    // 병원 상세 조회
    public HospitalResponse getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다. ID: " + id));
        return HospitalResponse.from(hospital);
    }

    // 특정 병원 소속 의사 목록 조회
    public List<Doctor> getDoctorsByHospitalId(Long hospitalId) {
        return doctorRepository.findByHospitalId(hospitalId);
    }
}