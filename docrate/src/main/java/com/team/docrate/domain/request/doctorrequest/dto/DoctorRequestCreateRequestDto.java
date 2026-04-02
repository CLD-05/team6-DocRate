package com.team.docrate.domain.request.doctorrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorRequestCreateRequestDto {

    @NotNull(message = "병원을 선택해주세요.")
    private Long hospitalId;

    @NotNull(message = "진료과를 선택해주세요.")
    private Long departmentId;

    @NotBlank(message = "의사 이름은 필수입니다.")
    @Size(max = 50, message = "의사 이름은 50자 이하로 입력해주세요.")
    private String name;

    @Size(max = 1000, message = "소개는 1000자 이하로 입력해주세요.")
    private String intro;
}
