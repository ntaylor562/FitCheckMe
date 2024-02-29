package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

public record UserCreateRequestDTO(
	String username,
	String bio
) {
	public static UserCreateRequestDTO toDTO(User user) {
		return new UserCreateRequestDTO(user.getUsername(), user.getBio());
	}
}
