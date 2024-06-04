package com.fitcheckme.FitCheckMe.DTOs;

import jakarta.validation.constraints.NotBlank;

public record FileUploadDTO(
	@NotBlank
	String fileName
) {}
