package com.fitcheckme.FitCheckMe.DTOs.User;

public record UserUpdateRequestDTO(
	Long userId,	
	String username,
	String bio
) {}
