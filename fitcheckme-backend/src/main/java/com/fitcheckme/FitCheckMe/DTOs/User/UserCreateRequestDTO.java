package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequestDTO(
	@NotBlank
	String username,
	@NotBlank
	String email,
	@NotBlank
	String password
) {
	public static UserCreateRequestDTO toDTO(User user) {
		return new UserCreateRequestDTO(user.getUsername(), user.getEmail(), user.getPassword());
	}
}
