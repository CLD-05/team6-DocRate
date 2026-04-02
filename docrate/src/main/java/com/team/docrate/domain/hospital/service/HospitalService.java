package com.team.docrate.domain.hospital.service;

import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    // 검색어(search), 카테고리(category), 페이징 정보를 받아 병원 목록 조회 (ACTIVE 상태 병원만)
    public Page<HospitalResponse> getHospitalList(String search, String category, Pageable pageable) {
        Page<Hospital> hospitalPage;

        if (search != null && !search.isEmpty()) {
            // 검색어(search)가 있는 경우: 이름 또는 주소로 검색
            hospitalPage = hospitalRepository.findByStatusAndSearch(HospitalStatus.ACTIVE, search, pageable);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리 필터가 있는 경우: 전처리 후 필터링 조회
            String processedCategory = category.toLowerCase().replaceAll("\\s+", "");
            hospitalPage = hospitalRepository.findActiveAndProcessedCategory(HospitalStatus.ACTIVE, processedCategory, pageable);
        } else {
            // 필터가 없는 경우: 전체 ACTIVE 상태인 병원 페이징 조회
            hospitalPage = hospitalRepository.findByStatus(HospitalStatus.ACTIVE, pageable);
        }

        // Page<Hospital>을 Page<HospitalResponse>로 변환
        List<HospitalResponse> hospitalResponses = hospitalPage.getContent().stream()
                .map(HospitalResponse::from)
                .collect(Collectors.toList());

        // PageImpl을 사용하여 Page 객체 생성 (총 요소 수, Pageable 정보 포함)
        return new PageImpl<>(hospitalResponses, pageable, hospitalPage.getTotalElements());
    }

    // 기본 목록 조회 (페이징 기본값 사용)
    public Page<HospitalResponse> getHospitalList(Pageable pageable) {
        return getHospitalList(null, null, pageable);
    }

    // 카테고리 필터링만 있는 경우 (페이징 기본값 사용)
    public Page<HospitalResponse> getHospitalListByCategory(String category, Pageable pageable) {
        return getHospitalList(category, pageable);
    }

 // 특정 병원 ID로 상세 정보 가져오기
    public HospitalResponse getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다. ID: " + id));
        return HospitalResponse.from(hospital);
    }
}