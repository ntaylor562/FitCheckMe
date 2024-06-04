package com.fitcheckme.FitCheckMe.DTOs.files;

public record FileUploadResponseDTO(
	Integer userId,
	Integer fileId,
	String fileName,
	String presignedURL
) {}
