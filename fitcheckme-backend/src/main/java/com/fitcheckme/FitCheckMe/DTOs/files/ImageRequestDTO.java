package com.fitcheckme.FitCheckMe.DTOs.files;

public record ImageRequestDTO(
	Integer userId,
	Integer fileId,
	String fileName
) {}
