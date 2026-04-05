package com.team.docrate.domain.hospital.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.docrate.domain.hospital.entity.Hospital;
import com.team.docrate.domain.hospital.enumtype.HospitalStatus;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Page<Hospital> findByStatus(HospitalStatus status, Pageable pageable);

    Page<Hospital> findByStatusAndNameContainingIgnoreCase(
            HospitalStatus status,
            String name,
            Pageable pageable
    );

    Optional<Hospital> findFirstByName(String name);

    Optional<Hospital> findByName(String name);

    // 모든 병원 조회
    Page<Hospital> findAll(Pageable pageable);

    // 이름으로만 검색
    @Query("SELECT h FROM Hospital h WHERE h.name LIKE %:search%")
    Page<Hospital> findByStatusAndSearch(
            @Param("status") HospitalStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT DISTINCT h.category FROM Hospital h WHERE h.category IS NOT NULL ORDER BY h.category")
    java.util.List<String> findDistinctCategories();

    // 카테고리로만 검색
    @Query("SELECT h FROM Hospital h WHERE LOWER(REPLACE(h.category, ' ', '')) = LOWER(REPLACE(:category, ' ', ''))")
    Page<Hospital> findActiveAndProcessedCategory(
            @Param("status") HospitalStatus status,
            @Param("category") String category,
            Pageable pageable
    );
    
    boolean existsByNameAndAddress(String name, String address);
}