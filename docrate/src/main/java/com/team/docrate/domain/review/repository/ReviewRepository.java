package com.team.docrate.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.team.docrate.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDoctorId(Long doctorId);

    Page<Review> findPageByDoctorId(Long doctorId, Pageable pageable);

    List<Review> findAllByDoctorId(Long doctorId);

    List<Review> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select round(avg(r.rating), 1) from Review r where r.doctor.id = :doctorId")
    Double findAverageRatingByDoctorId(Long doctorId);
}
