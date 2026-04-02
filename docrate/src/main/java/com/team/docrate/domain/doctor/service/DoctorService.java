package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorDetailResponse;
import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Page<DoctorResponse> getDoctors(String search, Pageable pageable) {
        String keyword = (search == null) ? "" : search.trim();

        return doctorRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(DoctorResponse::from);
    }

    public DoctorDetailResponse getDoctorDetail(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 의사를 찾을 수 없습니다. id=" + doctorId));

        return DoctorDetailResponse.from(doctor);
    }
}