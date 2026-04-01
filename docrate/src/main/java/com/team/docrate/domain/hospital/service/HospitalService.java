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

    // 카테고리, 페이징 정보를 받아 병원 목록 조회 (ACTIVE 상태 병원만, 대소문자/공백 무시 필터링)
    public Page<HospitalResponse> getHospitalList(String category, Pageable pageable) {
        Page<Hospital> hospitalPage;

        // 입력된 카테고리 값을 전처리 (소문자 변환, 모든 공백 제거)
        String processedCategory = null;
        if (category != null && !category.isEmpty()) {
            processedCategory = category.toLowerCase().replaceAll("\\s+", "");
        }

        if (processedCategory != null) {
            // 전처리된 카테고리와 ACTIVE 상태로 필터링하여 페이징 조회
        	hospitalPage = hospitalRepository.findActiveAndProcessedCategory(HospitalStatus.ACTIVE, processedCategory, pageable);
        } else {
            // 카테고리 필터가 없으면 ACTIVE 상태인 병원만 페이징하여 조회
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
        return getHospitalList(null, pageable); // 카테고리 없이 기본 페이징 조회
    }

    // 카테고리 필터링만 있는 경우 (페이징 기본값 사용)
    public Page<HospitalResponse> getHospitalListByCategory(String category, Pageable pageable) {
        return getHospitalList(category, pageable);
    }

    // 상세 조회, 검색 등 필요한 다른 서비스 메서드들은 추후 추가할 수 있습니다.
 // 상세 조회를 위한 메서드 추가
    public HospitalResponse getHospitalById(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다. ID: " + id));
        return HospitalResponse.from(hospital);
    }
}