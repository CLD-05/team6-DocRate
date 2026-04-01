package com.team.docrate.domain.review.repository;

import com.team.docrate.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDoctorId(Long doctorId);
    
 // 의사 ID로 모든 리뷰를 찾아서 생성일 내림차순(최신순)으로 정렬해 가져오는 메서드
    List<Review> findAllByDoctorIdOrderByCreatedAtDesc(Long doctorId);
}
