package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record OutfitUpdateImagesRequestDTO(
	@NotNull
	Integer outfitId,
	Set<Integer> addImageIds,
	Set<Integer> removeImageIds
) {}
