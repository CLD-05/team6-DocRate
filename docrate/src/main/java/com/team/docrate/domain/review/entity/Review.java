package com.team.docrate.domain.review.entity;

import java.math.BigDecimal;

import com.team.docrate.domain.doctor.entity.Doctor;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(nullable = false, precision = 2, scale = 1)
	private BigDecimal rating;

	@Column(nullable = false)
	private Integer bedsideManner;

	@Column(nullable = false)
	private Integer explanation;

	@Column(nullable = false)
	private Integer waitTime;

	@Column(nullable = false)
	private Boolean revisitIntention;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	public void edit(BigDecimal rating, Integer bedsideManner, Integer explanation, Integer waitTime,
			Boolean revisitIntention, String content) {
		this.rating = rating;
		this.bedsideManner = bedsideManner;
		this.explanation = explanation;
		this.waitTime = waitTime;
		this.revisitIntention = revisitIntention;
		this.content = content;
	}
}