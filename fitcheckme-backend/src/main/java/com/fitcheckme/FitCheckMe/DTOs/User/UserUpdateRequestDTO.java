package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

public record UserUpdateRequestDTO(
	Integer userId,	
	String username,
	String bio
) {
	public static UserUpdateRequestDTO toDTO(User user) {
		return new UserUpdateRequestDTO(user.getId(), user.getUsername(), user.getBio());
	}
}
