package com.fitcheckme.FitCheckMe.DTOs.Garment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GarmentUpdateRequestDTO(
	@NotNull
	Integer garmentId,
	@NotBlank
	String garmentName
) {}
