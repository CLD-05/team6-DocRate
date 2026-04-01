package com.team.docrate.domain.request.hospitalrequest.repository;

import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRequestRepository extends JpaRepository<HospitalRequest, Long> {
    // JpaRepository<엔티티타입, ID타입>을 상속받으면 save() 메서드가 자동으로 생깁니다.
}