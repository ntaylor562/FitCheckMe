package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

public record UserRequestDTO(
	Integer userId,
	String username,
	String bio
) {
	public static UserRequestDTO toDTO(User user) {
		return new UserRequestDTO(user.getId(), user.getUsername(), user.getBio());
	}
}
