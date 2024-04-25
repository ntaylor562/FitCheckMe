package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

public record UserCreateRequestDTO(
	String username,
	String email,
	String password
) {
	public static UserCreateRequestDTO toDTO(User user) {
		return new UserCreateRequestDTO(user.getUsername(), user.getEmail(), user.getPassword());
	}
}
