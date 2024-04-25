package com.fitcheckme.FitCheckMe.DTOs.auth;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO (
	@NotBlank
	String username,
	@NotBlank
	String password
) {}
