package com.fitcheckme.FitCheckMe.unit_tests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import com.fitcheckme.FitCheckMe.DTOs.User.JwtUserDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.auth.JwtUtil;
import com.fitcheckme.FitCheckMe.models.RefreshToken;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.RefreshTokenRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.services.AuthService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtil jwtUtil;

	private Integer refreshTokenValidity = 86400;

	@BeforeEach
	public void setup() {

	}

	private CustomUserDetails getUserDetails(Integer userId, String username, String password, Set<Role> authorities) {
		return new CustomUserDetails(userId, username, password, authorities);
	}

	private void verifyCommonAuthMethodCalls(VerificationMode mode) {
		Mockito.verify(userRepository, mode).findByUsernameIgnoreCase(any());
		Mockito.verify(jwtUtil, mode).createToken(any());
		Mockito.verify(refreshTokenRepository, mode).save(any());
	}

	@Test
	public void testUserLoginAndExpectTokens() {
		ReflectionTestUtils.setField(authService, "refreshTokenValidity", this.refreshTokenValidity);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		LocalDateTime refreshTokenExpiration = LocalDateTime.now().plusSeconds(refreshTokenValidity);

		Mockito.when(authenticationManager.authenticate(any()))
				.thenReturn(new UsernamePasswordAuthenticationToken("user1", ""));
		Mockito.when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(Optional.of(user1));
		Mockito.when(jwtUtil.createToken(JwtUserDTO.toDTO(user1))).thenReturn("accessToken");
		Mockito.when(refreshTokenRepository.save(any()))
				.thenReturn(new RefreshToken("refreshToken", user1, refreshTokenExpiration));

		UserLoginReturnDTO result = authService.userLogin(new UserLoginRequestDTO("user1", "password1"));

		assertThat(result).isNotNull()
				.isEqualTo(
						new UserLoginReturnDTO(new UserRequestDTO(user1.getId(), user1.getUsername(), user1.getBio()),
								"accessToken", "refreshToken"));
		
		verifyCommonAuthMethodCalls(Mockito.times(1));
	}

	@Test
	public void givenBadCredentials_whenUserLogin_thenExpectBadCredentialsException() {
		Mockito.when(authenticationManager.authenticate(any()))
				.thenThrow(BadCredentialsException.class);

		assertThatExceptionOfType(BadCredentialsException.class)
				.isThrownBy(() -> authService.userLogin(new UserLoginRequestDTO("user1", "password1")));
		
		verifyCommonAuthMethodCalls(Mockito.never());
	}

	@Test
	public void testRefreshTokenAndExpectTokens() {
		ReflectionTestUtils.setField(authService, "refreshTokenValidity", this.refreshTokenValidity);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		LocalDateTime refreshTokenExpiration = LocalDateTime.now().plusSeconds(refreshTokenValidity);

		Mockito.when(jwtUtil.createToken(JwtUserDTO.toDTO(user1))).thenReturn("accessToken");
		// Old refresh token
		Mockito.when(refreshTokenRepository.findByRefreshToken("oldRefreshToken"))
				.thenReturn(Optional.of(new RefreshToken("oldRefreshToken", user1, refreshTokenExpiration)));
		// New refresh token
		Mockito.when(refreshTokenRepository.save(any()))
				.thenReturn(new RefreshToken("refreshToken", user1, refreshTokenExpiration));

		UserLoginReturnDTO result = authService.refreshToken("oldRefreshToken");

		assertThat(result).isNotNull()
				.isEqualTo(new UserLoginReturnDTO(UserRequestDTO.toDTO(user1), "accessToken", "refreshToken"));
		
		Mockito.verify(refreshTokenRepository, Mockito.times(1)).save(any());
		Mockito.verify(refreshTokenRepository, Mockito.times(1)).delete(any());
	}

	@Test
	public void testRefreshTokenAndExpectEntityNotFoundException() {
		Mockito.when(refreshTokenRepository.findByRefreshToken("oldRefreshToken"))
				.thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> authService.refreshToken("oldRefreshToken"))
				.withMessage("Refresh token not found");

		Mockito.verify(refreshTokenRepository, Mockito.never()).delete(any());
		Mockito.verify(refreshTokenRepository, Mockito.never()).save(any());
	}

	@Test
	public void testRefreshTokenAndExpectExpiredToken() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		RefreshToken oldRefreshToken = new RefreshToken("refreshToken", user1, LocalDateTime.now().minusSeconds(100));
		Mockito.when(refreshTokenRepository.findByRefreshToken("refreshToken")).thenReturn(
				Optional.of(oldRefreshToken));

		assertThatExceptionOfType(RuntimeException.class)
				.isThrownBy(() -> authService.refreshToken("refreshToken"))
				.withMessage("Refresh token expired");

		Mockito.verify(refreshTokenRepository, Mockito.times(1)).delete(oldRefreshToken);
		Mockito.verify(refreshTokenRepository, Mockito.never()).save(any());
	}

	@Test
	public void testDeleteRefreshToken() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);

		Mockito.when(
				refreshTokenRepository.findByUserIdAndRefreshToken(userDetails.getUserId(), "refreshToken"))
				.thenReturn(Optional.of(new RefreshToken("refreshToken", user1, LocalDateTime.now())));

		assertThatNoException().isThrownBy(() -> authService.deleteRefreshToken("refreshToken", userDetails));
		Mockito.verify(refreshTokenRepository, Mockito.times(1)).delete(any());
	}

	@Test
	public void testDeleteRefreshTokenAndExpectEntityNotFoundException() {
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);

		Mockito.when(refreshTokenRepository.findByUserIdAndRefreshToken(1, "refreshToken"))
				.thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> authService.deleteRefreshToken("refreshToken", userDetails))
				.withMessage("Refresh token not found");

		Mockito.verify(refreshTokenRepository, Mockito.never()).delete(any());
	}
}
