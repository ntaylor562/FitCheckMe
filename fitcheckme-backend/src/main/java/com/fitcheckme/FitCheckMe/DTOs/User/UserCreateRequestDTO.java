package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

public record UserCreateRequestDTO(
	String username
) {
	public static UserCreateRequestDTO toDTO(User user) {
		return new UserCreateRequestDTO(user.getUsername());
	}
}
