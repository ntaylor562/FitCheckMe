package com.fitcheckme.FitCheckMe.DTOs.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateDTO(
	@NotNull
	Integer userId,
	@NotBlank
	String role
) {}
