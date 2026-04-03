package com.team.docrate.domain.request.doctorrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorRequestCreateRequestDto {

    @NotBlank(message = "의사 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "병원명을 입력해주세요.")
    private String hospitalName;

    @NotNull(message = "진료과를 선택해주세요.")
    private Long departmentId;

    private String intro;
}