package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorDto;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository = null;

    // 의사 목록 조회
    public List<DoctorDto> getDoctors(Long hospitalId, Long departmentId) {
        return doctorRepository.findByCondition(hospitalId, departmentId)
                .stream()
                .map(DoctorDto::from)
                .toList();
    }

    // 의사 상세 조회
    public DoctorDto getDoctorDetail(Long doctorId) {
        Doctor doctor = doctorRepository.findDetailById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("의사를 찾을 수 없습니다."));

        return DoctorDto.from(doctor);
    }

    // 병원 기준 의사 조회
    public List<DoctorDto> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalId(hospitalId)
                .stream()
                .map(DoctorDto::from)
                .toList();
    }

    // 진료과 기준 의사 조회
    public List<DoctorDto> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId)
                .stream()
                .map(DoctorDto::from)
                .toList();
    }
}