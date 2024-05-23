package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.Set;
import java.util.stream.Collectors;

import com.fitcheckme.FitCheckMe.models.Outfit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OutfitCreateRequestDTO(
		@NotBlank String outfitName,
		@NotNull String outfitDesc,
		Set<Integer> outfitTags,
		Set<Integer> garments) {
	public static OutfitCreateRequestDTO toDTO(Outfit outfit) {
		return new OutfitCreateRequestDTO(outfit.getName(), outfit.getDesc(),
				outfit.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet()),
				outfit.getGarments().stream().map(garment -> garment.getId()).collect(Collectors.toSet()));
	}
}
