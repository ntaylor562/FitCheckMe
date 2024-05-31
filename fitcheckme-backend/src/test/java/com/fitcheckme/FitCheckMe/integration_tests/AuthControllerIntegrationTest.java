package com.fitcheckme.FitCheckMe.integration_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fitcheckme.FitCheckMe.DTOs.ExceptionResponseDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.auth.JwtUtil;
import com.fitcheckme.FitCheckMe.repositories.RefreshTokenRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import io.jsonwebtoken.Claims;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	private String getCookieFromResponse(ResponseEntity<?> response, String cookieName) {
		List<String> cookies = response.getHeaders().get("Set-Cookie");
		if(cookies == null) return null;
		for(String cookie : cookies) {
			if(cookie.contains(cookieName)) {
				return cookie.split(";")[0].replace(cookieName + "=", "");
			}
		}
		return null;
	}

	@Test
	public void testLogin() {
		UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test_user", "test");

		ResponseEntity<Object> response = postCall("/api/auth/login", requestDTO);
		UserLoginReturnDTO returnDTO = getObjectFromResponse(response, UserLoginReturnDTO.class);

		String accessToken = this.getCookieFromResponse(response, "jwt-access-token");
		String refreshToken = this.getCookieFromResponse(response, "jwt-refresh-token");

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(returnDTO.user().username()).isEqualTo("test_user");
		assertThat(accessToken).isNotNull();
		assertThat(refreshToken).isNotNull();
		assertThat(refreshTokenRepository.existsByRefreshToken(refreshToken)).isTrue();

		Claims claims = jwtUtil.parseJwtClaims(accessToken);
		assertThatNoException().isThrownBy(() -> jwtUtil.validateClaims(claims));
		assertThat(jwtUtil.getUsername(claims)).isEqualTo("test_user");
	}

	@Test
	public void testLoginAndExpectBadCredentials() {
		UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test_user", "wrong_password");

		ResponseEntity<Object> response = postCall("/api/auth/login", requestDTO, true);
		ExceptionResponseDTO responseBody = getObjectFromResponse(response, ExceptionResponseDTO.class);

		assertThat(response.getStatusCode().isError()).isTrue();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(responseBody.message()).isEqualTo("Bad credentials");
	}
}
