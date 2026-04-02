package com.team.docrate.domain.hospital.dto;

import com.team.docrate.domain.hospital.entity.Hospital;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HospitalListItemDto {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String category;

    public static HospitalListItemDto from(Hospital hospital) {
        return HospitalListItemDto.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .phone(hospital.getPhone())
                .category(hospital.getCategory())
                .build();
    }
}