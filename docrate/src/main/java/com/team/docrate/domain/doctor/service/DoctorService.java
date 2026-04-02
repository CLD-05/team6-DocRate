package com.team.docrate.domain.doctor.service;

import com.team.docrate.domain.doctor.dto.DoctorResponse;
import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.doctor.enumtype.DoctorStatus;
import com.team.docrate.domain.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public Page<DoctorResponse> getDoctors(Long hospitalId, Long departmentId, Pageable pageable) {
        Page<Doctor> doctorPage;

        if (hospitalId != null && departmentId != null) {
            doctorPage = doctorRepository.findByHospitalIdAndDepartmentIdAndStatus(
                    hospitalId, departmentId, DoctorStatus.ACTIVE, pageable
            );
        } else if (hospitalId != null) {
            doctorPage = doctorRepository.findByHospitalIdAndStatus(
                    hospitalId, DoctorStatus.ACTIVE, pageable
            );
        } else if (departmentId != null) {
            doctorPage = doctorRepository.findByDepartmentIdAndStatus(
                    departmentId, DoctorStatus.ACTIVE, pageable
            );
        } else {
            doctorPage = doctorRepository.findByStatus(
                    DoctorStatus.ACTIVE, pageable
            );
        }

        return doctorPage.map(DoctorResponse::from);
    }
}