package com.fitcheckme.FitCheckMe.DTOs.auth;

import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;

import jakarta.validation.constraints.NotNull;

public record UserLoginReturnDTO (
	@NotNull
	UserRequestDTO user,
	@NotNull
	String accessToken,
	@NotNull
	String refreshToken
){}
