package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
	
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@PostMapping("login")
	@ResponseStatus(HttpStatus.OK)
	public void login(@Valid @RequestBody UserLoginRequestDTO user, HttpServletResponse response) {
		UserLoginReturnDTO userLoginReturnDTO = authService.userLogin(user);
		Cookie cookie = new Cookie("jwt-token", userLoginReturnDTO.token());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setSecure(true);
		response.addCookie(cookie);
	}

	@PostMapping("logout")
	@ResponseStatus(HttpStatus.OK)
	public void logout(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) {
		//Deleting cookie
		Cookie cookie = new Cookie("jwt-token", null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
	
}
