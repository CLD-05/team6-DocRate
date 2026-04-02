package com.team.docrate.domain.hospital.repository;

import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    // 이름으로 검색 (기존 메서드)
    List<Hospital> findByNameContaining(String keyword);

    // 상태로 페이징 조회 (ACTIVE 병원만)
    Page<Hospital> findByStatus(HospitalStatus status, Pageable pageable);

    // 이름 또는 주소로 검색 + 상태 필터링 (와이어프레임 검색 기능 지원)
    @Query("SELECT h FROM Hospital h WHERE h.status = :status AND (h.name LIKE %:search% OR h.address LIKE %:search%)")
    Page<Hospital> findByStatusAndSearch(@Param("status") HospitalStatus status, @Param("search") String search, Pageable pageable);

    // 상태 및 카테고리 (대소문자, 공백 무시)로 페이징 조회 (JPQL 사용)
    @Query("SELECT h FROM Hospital h WHERE h.status = :status AND LOWER(REPLACE(h.category, ' ', '')) = LOWER(REPLACE(:category, ' ', ''))")
    Page<Hospital> findActiveAndProcessedCategory(
    		@Param("status") HospitalStatus status,
            @Param("category") String category,
            Pageable pageable
    );
}
