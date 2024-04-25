package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

public record UserUpdateDetailsRequestDTO(
	Integer userId,	
	String username,
	String bio
) {
	public static UserUpdateDetailsRequestDTO toDTO(User user) {
		return new UserUpdateDetailsRequestDTO(user.getId(), user.getUsername(), user.getBio());
	}
}
