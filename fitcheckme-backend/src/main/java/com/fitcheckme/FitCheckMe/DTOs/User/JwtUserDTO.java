package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JwtUserDTO (
	@NotNull
	Integer userId,
	@NotBlank
	String username,
	@NotBlank
	String email
){
	public static JwtUserDTO toDTO(User user) {
		return new JwtUserDTO(user.getId(), user.getUsername(), user.getEmail());
	}
}