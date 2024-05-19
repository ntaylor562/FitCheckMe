package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitcheckme.FitCheckMe.models.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>{
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	Optional<RefreshToken> findByUser_UsernameAndRefreshToken(String username, String refreshToken);

	@Modifying
	@Query(value = "DELETE FROM app.refresh_token WHERE user_id = ?1", nativeQuery = true)
	void deleteAllByUserId(Integer userId);
}
