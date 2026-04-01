package com.team.docrate.domain.request.doctorrequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team.docrate.domain.request.doctorrequest.entity.DoctorRequest;

@Repository
public interface DoctorRequestRepository extends JpaRepository<DoctorRequest, Long> {


}
