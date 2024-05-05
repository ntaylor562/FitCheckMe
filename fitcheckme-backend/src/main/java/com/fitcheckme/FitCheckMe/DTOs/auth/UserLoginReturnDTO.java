package com.fitcheckme.FitCheckMe.DTOs.auth;

import jakarta.validation.constraints.NotNull;

public record UserLoginReturnDTO (
	@NotNull
	String username,
	@NotNull
	String accessToken,
	@NotNull
	String refreshToken
){}
