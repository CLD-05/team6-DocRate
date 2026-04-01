package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorResponse> getDoctors(Long hospitalId, Long departmentId) {
        return doctorRepository.findDoctors(hospitalId, departmentId)
                .stream()
                .map(DoctorResponse::from)
                .toList();
    }
}