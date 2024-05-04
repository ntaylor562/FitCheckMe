package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import com.fitcheckme.FitCheckMe.models.Outfit;

import jakarta.validation.constraints.NotBlank;

public record OutfitCreateRequestDTO (
	@NotBlank
	String outfitName,
	@NotBlank
	String outfitDesc,
	List<Integer> outfitTags,
	List<Integer> garments
) {
	public static OutfitCreateRequestDTO toDTO(Outfit outfit) {
		//return new OutfitCreateRequestDTO(outfit.getUser().getId(), outfit.getName(), outfit.getDesc(), outfit.getTags(), outfit.getGarments().stream().map(garment -> GarmentCreateRequestDTO.toDTO(garment)), )
		return new OutfitCreateRequestDTO(outfit.getName(), outfit.getDesc(), outfit.getTags().stream().map(tag -> tag.getId()).toList(), outfit.getGarments().stream().map(garment -> garment.getId()).toList());
	}
}
