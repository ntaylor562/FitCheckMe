package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record GarmentURLUpdateRequestDTO(
	@NotNull
	Integer garmentId,
	List<String> addURLs,
	List<String> removeURLs
) {}
