package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.JwtUserDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.auth.JwtUtil;
import com.fitcheckme.FitCheckMe.models.RefreshToken;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.RefreshTokenRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

	@Value("${fitcheckme.jwt-refresh-token-validity-s}")
	private Integer refreshTokenValidity;
	
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public AuthService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Transactional
	private RefreshToken createRefreshToken(User user) {
		RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(), user, LocalDateTime.now().plusSeconds(refreshTokenValidity));
		refreshTokenRepository.save(refreshToken);
		return refreshToken;
	}

	@Transactional
	public UserLoginReturnDTO userLogin(UserLoginRequestDTO userDTO) throws BadCredentialsException {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password()));
		User user = userRepository.findByUsernameIgnoreCase(authentication.getName()).get();
		String accessToken = jwtUtil.createToken(JwtUserDTO.toDTO(user));
		RefreshToken refreshToken = this.createRefreshToken(user);

		return new UserLoginReturnDTO(UserRequestDTO.toDTO(user), accessToken, refreshToken.getRefreshToken());
	}

	@Transactional
	public UserLoginReturnDTO refreshToken(String refreshToken) throws RuntimeException {
		//TODO decide whether to require access token to refresh token
		//Validating the token exists and is not expired
		RefreshToken oldRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("Refresh token not found"));
		if(oldRefreshToken.getExpireDate().isBefore(LocalDateTime.now())) {
			refreshTokenRepository.delete(oldRefreshToken);
			throw new RuntimeException("Refresh token expired");
		}
		User user = oldRefreshToken.getUser();
		String accessToken = jwtUtil.createToken(JwtUserDTO.toDTO(user));
		RefreshToken newRefreshToken = this.createRefreshToken(user);
		refreshTokenRepository.delete(oldRefreshToken);
		return new UserLoginReturnDTO(UserRequestDTO.toDTO(user), accessToken, newRefreshToken.getRefreshToken());
	}

	@Transactional
	public void deleteRefreshToken(String refreshToken, UserDetails userDetails) throws RuntimeException {
		refreshTokenRepository.delete(refreshTokenRepository.findByUser_UsernameAndRefreshToken(userDetails.getUsername(), refreshToken).orElseThrow(() -> new RuntimeException("Refresh token not found")));
	}

}
