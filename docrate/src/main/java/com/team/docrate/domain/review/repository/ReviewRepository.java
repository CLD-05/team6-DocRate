package com.team.docrate.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team.docrate.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
	List<Review> findByDoctorId(Long doctorId);
    
 // 페이징 처리
    Page<Review> findPageByDoctorId(Long doctorId, Pageable pageable);
    
 // 전체리스트(평균 계산)
    List<Review> findAllByDoctorId(Long doctorId);

 // 리뷰 ID와 작성자 ID가 일치하는 리뷰를 찾는 메서드 (권한 확인용)
    Optional<Review> findByIdAndUserId(Long id, Long userId);
}
