package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.time.LocalDateTime;
import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Outfit;

public record OutfitRequestDTO (
	Integer outfitId,
	Integer userId,
	String outfitName,
	String outfitDesc,
	LocalDateTime creationDate,
	List<GarmentRequestDTO>garments
) {
	public static OutfitRequestDTO toDTO(Outfit outfit) {
		return new OutfitRequestDTO(
			outfit.getId(),
			outfit.getUser().getId(),
			outfit.getName(), outfit.getDesc(),
			outfit.getCreationDate(),
			outfit.getGarments().stream().map(garment -> new GarmentRequestDTO(
				garment.getId(),
				garment.getName(),
				garment.getUser().getId(),
				garment.getOutfits().stream().map(o -> o.getId()).toList(),
				garment.getURLs(),
				garment.getTags().stream().map(t -> new TagRequestDTO(t.getId(), t.getTagName())).toList())
			).toList()
		);
	}
}
