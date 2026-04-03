package com.team.docrate.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.team.docrate.domain.review.entity.Review;
import com.team.docrate.domain.user.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDoctorId(Long doctorId);

    Page<Review> findPageByDoctorId(Long doctorId, Pageable pageable);

    List<Review> findAllByDoctorId(Long doctorId);

    List<Review> findAllByUserIdOrderByCreatedAtDesc(Long userId);


    @Query("select round(avg(r.rating), 1) from Review r where r.doctor.id = :doctorId")
    Double findAverageRatingByDoctorId(Long doctorId);
 // 리뷰 ID와 작성자 ID가 일치하는 리뷰를 찾는 메서드 (권한 확인용)
    Optional<Review> findByIdAndUserId(Long id, Long userId);

}



