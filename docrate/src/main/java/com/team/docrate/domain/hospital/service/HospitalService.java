package com.team.docrate.domain.hospital.service;

import com.team.docrate.domain.hospital.dto.HospitalResponse;
import com.team.docrate.domain.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public List<HospitalResponse> getHospitalList() {
        return hospitalRepository.findAll()
                .stream()
                .map(HospitalResponse::from)
                .toList();
    }
}