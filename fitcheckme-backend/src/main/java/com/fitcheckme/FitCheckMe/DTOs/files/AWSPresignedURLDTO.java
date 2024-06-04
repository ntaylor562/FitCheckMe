package com.fitcheckme.FitCheckMe.DTOs.files;

public record AWSPresignedURLDTO(
	String fileName,
	String presignedURL
) {}
