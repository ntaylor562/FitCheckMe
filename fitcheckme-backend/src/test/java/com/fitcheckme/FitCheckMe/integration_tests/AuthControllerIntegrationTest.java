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

import io.jsonwebtoken.Claims;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {
	@Autowired
	private JwtUtil jwtUtil;

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

	@Test
	public void testLogout() {
		UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test_user", "test");

		ResponseEntity<Object> loginResponse = postCall("/api/auth/login", requestDTO);
		String accessToken = this.getCookieFromResponse(loginResponse, "jwt-access-token");
		String refreshToken = this.getCookieFromResponse(loginResponse, "jwt-refresh-token");

		addAuthTokensToRestTemplate(accessToken, refreshToken);

		ResponseEntity<Object> logoutResponse = postCall("/api/auth/logout", null);
		String deletedRefreshToken = this.getCookieFromResponse(logoutResponse, "jwt-refresh-token");

		assertThat(logoutResponse.getStatusCode().isError()).isFalse();
		assertThat(deletedRefreshToken).isEqualTo("");
		assertThat(refreshTokenRepository.existsByRefreshToken(refreshToken)).isFalse();
	}

	@Test
	public void testLogoutWithNoCookiesAndExpectOK() {
		removeAuthTokensFromRestTemplate();

		ResponseEntity<Object> response = postCall("/api/auth/logout", null);

		assertThat(response.getStatusCode().isError()).isFalse();
	}
	
	@Test
	public void testRefreshToken() {
		UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test_user", "test");

		ResponseEntity<Object> loginResponse = postCall("/api/auth/login", requestDTO);
		String accessToken = this.getCookieFromResponse(loginResponse, "jwt-access-token");
		String refreshToken = this.getCookieFromResponse(loginResponse, "jwt-refresh-token");

		addAuthTokensToRestTemplate(accessToken, refreshToken);

		ResponseEntity<Object> response = postCall("/api/auth/refresh", null);
		String newAccessToken = this.getCookieFromResponse(response, "jwt-access-token");
		String newRefreshToken = this.getCookieFromResponse(response, "jwt-refresh-token");

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(newAccessToken).isNotNull();
		assertThat(refreshTokenRepository.existsByRefreshToken(refreshToken)).isFalse();
		assertThat(refreshTokenRepository.existsByRefreshToken(newRefreshToken)).isTrue();
		assertThat(refreshToken).isNotEqualTo(newRefreshToken);

		Claims claims = jwtUtil.parseJwtClaims(newAccessToken);
		assertThatNoException().isThrownBy(() -> jwtUtil.validateClaims(claims));
		assertThat(jwtUtil.getUsername(claims)).isEqualTo("test_user");
	}

	@Test
	public void testRefreshTokenWithNoCookiesAndExpectUnauthorized() {
		removeAuthTokensFromRestTemplate();

		ResponseEntity<Object> response = postCall("/api/auth/refresh", null, true);

		assertThat(response.getStatusCode().isError()).isTrue();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testIsAuthenticatedAndExpectTrue() {
		login("test_user");

		ResponseEntity<Object> response = getCall("/api/auth/isAuthenticated");

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(response.getBody()).isEqualTo(true);
	}

	@Test
	public void testIsAuthenticatedAndExpectUnauthorized() {
		ResponseEntity<Object> response = getCall("/api/auth/isAuthenticated", true);

		assertThat(response.getStatusCode().isError()).isTrue();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
