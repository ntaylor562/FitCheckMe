package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record GarmentURLUpdateRequestDTO(
	@NotNull
	Integer garmentId,
	Set<String> addURLs,
	Set<String> removeURLs
) {}
