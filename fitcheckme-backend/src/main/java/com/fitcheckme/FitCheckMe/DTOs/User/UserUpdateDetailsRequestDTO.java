package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

import jakarta.validation.constraints.NotNull;

public record UserUpdateDetailsRequestDTO(
	@NotNull
	Integer userId,	
	String username,
	String bio,
	Integer profilePictureId
) {
	public static UserUpdateDetailsRequestDTO toDTO(User user) {
		return new UserUpdateDetailsRequestDTO(user.getId(), user.getUsername(), user.getBio(), user.getProfilePicture().getId());
	}
}
