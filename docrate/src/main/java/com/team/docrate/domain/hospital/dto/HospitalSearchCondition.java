package com.team.docrate.domain.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalSearchCondition {
    private String search;   // 이름 또는 주소 검색어
    private String category; // 진료과목 필터
    private String region;   // 지역 필터 (필요 시 확장)
}
