package com.fitcheckme.FitCheckMe.DTOs.User;

public record UserUpdateRequestDTO(
	Integer userId,	
	String username,
	String bio
) {}
