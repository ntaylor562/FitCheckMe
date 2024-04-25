package com.fitcheckme.FitCheckMe.DTOs.User;

import com.fitcheckme.FitCheckMe.models.User;

import jakarta.validation.constraints.NotBlank;

public record JwtUserDTO (
	@NotBlank
	String username,
	@NotBlank
	String email
){
	public static JwtUserDTO toDTO(User user) {
		return new JwtUserDTO(user.getUsername(), user.getEmail());
	}
}