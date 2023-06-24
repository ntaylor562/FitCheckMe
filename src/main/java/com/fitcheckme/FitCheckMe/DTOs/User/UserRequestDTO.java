package com.fitcheckme.FitCheckMe.DTOs.User;

public record UserRequestDTO(
	Integer userId,
	String username,
	String bio
) {}
