package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>{
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	Optional<RefreshToken> findByUser_UsernameAndRefreshToken(String username, String refreshToken);
}
