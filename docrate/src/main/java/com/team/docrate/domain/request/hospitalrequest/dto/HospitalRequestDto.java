package com.team.docrate.domain.request.hospitalrequest.dto;

import lombok.*;

@Getter
@Setter  // HTML 데이터를 담기 위해 Setter가 필요합니다.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalRequestDto {
    private String name;     // 병원명
    private String category; // 카테고리
    private String address;  // 주소
    private String phone;    // 전화번호
}