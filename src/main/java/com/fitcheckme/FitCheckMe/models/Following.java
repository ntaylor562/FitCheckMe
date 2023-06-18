package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "following", schema = "community")
public record Following(
    @Id
	@NotBlank
	@ManyToOne
	@JoinColumn(name = "follower_id", nullable = false)
    User follower,

    @Id
	@NotBlank
	@ManyToOne
	@JoinColumn(name = "followee_id", nullable = false)
    User followee,

	@NotBlank
	@Column(name = "follow_date")
	LocalDateTime followDate
) {}
