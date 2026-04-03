package com.team.docrate.domain.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByNameContaining(String keyword);

	List<Hospital> findAll();

	Optional<Hospital> findByName(String string);

	    // 모든 병원 조회 (상태 필터링 없음) - 이전 findByStatus 대신 일반 목록 조회를 위해 사용
    Page<Hospital> findAll(Pageable pageable);

    // 이름으로만 검색 + 상태 필터링 제거
    // Original: WHERE h.status = :status AND h.name LIKE %:search%
    @Query("SELECT h FROM Hospital h WHERE h.name LIKE %:search%")
    Page<Hospital> findByStatusAndSearch(@Param("status") HospitalStatus status, @Param("search") String search, Pageable pageable); // Note: status parameter is kept but not used in the query.

    // 카테고리로만 검색 (상태 필터링 제거)
    // Original: WHERE h.status = :status AND LOWER(REPLACE(h.category, ' ', '')) = LOWER(REPLACE(:category, ' ', ''))
    @Query("SELECT h FROM Hospital h WHERE LOWER(REPLACE(h.category, ' ', '')) = LOWER(REPLACE(:category, ' ', ''))")
    Page<Hospital> findActiveAndProcessedCategory(
    		@Param("status") HospitalStatus status, // Note: status parameter is kept but not used in the query.
            @Param("category") String category,
            Pageable pageable
    );
	
}
