package com.fitcheckme.FitCheckMe.DTOs.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdatePasswordRequestDTO (
	@NotNull Integer userId,
	@NotBlank String oldPassword,
	@NotBlank String newPassword
){}
