package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record OutfitGarmentUpdateRequestDTO(
	@NotNull
	Integer outfitId,
	List<Integer> addGarmentIds,
	List<Integer> removeGarmentIds
) {}
