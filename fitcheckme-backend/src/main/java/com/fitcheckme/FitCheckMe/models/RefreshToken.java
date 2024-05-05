package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_token", schema = "app")
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Integer refreshTokenId;

	@Column(name = "refresh_token", nullable = false)
	private String refreshToken;

	@Column(name = "expire_date", nullable = false)
	private LocalDateTime expireDate;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public RefreshToken() {

	}

	public RefreshToken(String refreshToken, User user, LocalDateTime expireDate) {
		this.refreshToken = refreshToken;
		this.user = user;
		this.expireDate = expireDate;
	}

	public Integer getRefreshTokenId() {
		return this.refreshTokenId;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public LocalDateTime getExpireDate() {
		return this.expireDate;
	}

	public User getUser() {
		return this.user;
	}
}
