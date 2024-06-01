package com.fitcheckme.FitCheckMe.DTOs.AWS;

public record AWSPresignedURLDTO(
	String fileName,
	String presignedURL
) {}
