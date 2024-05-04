package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import jakarta.validation.constraints.NotNull;

public record OutfitUpdateRequestDTO (
	@NotNull
	Integer outfitId,
	String outfitName,
	String outfitDesc
) {}
