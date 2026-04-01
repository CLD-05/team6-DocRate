package com.team.docrate.domain.hospital.dto;

import com.team.docrate.domain.hospital.entity.Hospital;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HospitalResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String category;
    private String status;
    private String department; // 1. 필드 추가

    public static HospitalResponse from(Hospital hospital) {
        return HospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .phone(hospital.getPhone())
                .category(hospital.getCategory())
                .status(hospital.getStatus().name())
                .department(hospital.getCategory()) // 2. 데이터 매핑 (일단 category 값을 넣어주면 에러가 사라집니다)
                .build();
    }
}