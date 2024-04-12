package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import com.fitcheckme.FitCheckMe.models.Outfit;

public record OutfitCreateRequestDTO (
	Integer userId,
	String outfitName,
	String outfitDesc,
	List<Integer> outfitTags,
	List<Integer> garments
) {
	public static OutfitCreateRequestDTO toDTO(Outfit outfit) {
		//return new OutfitCreateRequestDTO(outfit.getUser().getId(), outfit.getName(), outfit.getDesc(), outfit.getTags(), outfit.getGarments().stream().map(garment -> GarmentCreateRequestDTO.toDTO(garment)), )
		return new OutfitCreateRequestDTO(outfit.getUser().getId(), outfit.getName(), outfit.getDesc(), outfit.getTags().stream().map(tag -> tag.getId()).toList(), outfit.getGarments().stream().map(garment -> garment.getId()).toList());
	}
}
