package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record GarmentUpdateImagesRequestDTO(
	@NotNull
	Integer garmentId,
	Set<Integer> addImageIds,
	Set<Integer> removeImageIds
) {}
