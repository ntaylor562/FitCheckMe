package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return Optional.empty();
		Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(name)).findFirst().orElse(null);
		return cookie != null ? Optional.of(cookie) : Optional.empty();
	}

	private Cookie getAccessTokenCookie(String token) {
		Cookie cookie = new Cookie("jwt-access-token", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setSecure(true);
		return cookie;
	}

	private Cookie getRefreshTokenCookie(String token) {
		Cookie cookie = new Cookie("jwt-refresh-token", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/api/auth/refresh");
		cookie.setSecure(true);
		return cookie;
	}

	private Cookie getDeleteCookie(String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setMaxAge(0);
		return cookie;
	}

	@PostMapping("login")
	@ResponseStatus(HttpStatus.OK)
	public void login(@Valid @RequestBody UserLoginRequestDTO user, HttpServletResponse response) {
		UserLoginReturnDTO userLoginReturnDTO = authService.userLogin(user);
		response.addCookie(this.getAccessTokenCookie(userLoginReturnDTO.accessToken()));
		response.addCookie(this.getRefreshTokenCookie(userLoginReturnDTO.refreshToken()));
	}

	@PostMapping("logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) {
		//Deleting refresh token and refresh token cookie
		Cookie refreshTokenCookie = this.getCookie(request, "jwt-refresh-token").orElse(null);
		if(refreshTokenCookie != null) {
			try {
				authService.deleteRefreshToken(refreshTokenCookie.getValue(), userDetails);
				response.addCookie(getDeleteCookie("jwt-refresh-token"));
			}
			catch(RuntimeException e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		}

		//Deleting access token cookie
		response.addCookie(getDeleteCookie("jwt-access-token"));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("refresh")
	public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie oldRefreshTokenCookie = this.getCookie(request, "jwt-refresh-token").orElse(null);
		if(oldRefreshTokenCookie == null) return new ResponseEntity<String>("No refresh token provided", HttpStatus.BAD_REQUEST);

		UserLoginReturnDTO userLoginReturnDTO;
		try {
			userLoginReturnDTO = authService.refreshToken(oldRefreshTokenCookie.getValue());
		}
		catch(RuntimeException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		Cookie accessTokenCookie = getAccessTokenCookie(userLoginReturnDTO.accessToken());
		Cookie refreshTokenCookie = getRefreshTokenCookie(userLoginReturnDTO.refreshToken());

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("isAuthenticated")
	public ResponseEntity<Boolean> isAuthenticated() {
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
	
}
