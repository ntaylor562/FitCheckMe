package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "following", schema = "community")
public class Following{
    @Id
	@ManyToOne
	@JoinColumn(name = "follower_id", nullable = false)
    User follower;

    @Id
	@ManyToOne
	@JoinColumn(name = "followee_id", nullable = false)
    User followee;

	@Column(name = "follow_date", nullable = false)
	LocalDateTime followDate;
}
