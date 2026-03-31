package com.team.docrate.domain.review.entity;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "doctor_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private Integer bedsideManner;

    @Column(nullable = false)
    private Integer explanation;

    @Column(nullable = false)
    private Integer waitTime;

    @Column(nullable = false)
    private Integer revisitIntention;

    @Column(nullable = false, length = 2000)
    private String content;
}