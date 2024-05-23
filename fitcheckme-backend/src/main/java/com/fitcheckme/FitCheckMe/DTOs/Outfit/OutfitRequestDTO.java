package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Outfit;

public record OutfitRequestDTO (
	Integer outfitId,
	Integer userId,
	String outfitName,
	String outfitDesc,
	LocalDateTime creationDate,
	Set<TagRequestDTO> outfitTags,
	Set<GarmentRequestDTO>garments
) {
	public static OutfitRequestDTO toDTO(Outfit outfit) {
		return new OutfitRequestDTO(
			outfit.getId(),
			outfit.getUser().getId(),
			outfit.getName(), outfit.getDesc(),
			outfit.getCreationDate(),
			outfit.getTags().stream().map(t -> new TagRequestDTO(t.getId(), t.getName())).collect(Collectors.toSet()),
			outfit.getGarments().stream().map(garment -> new GarmentRequestDTO(
				garment.getId(),
				garment.getName(),
				garment.getUser().getId(),
				garment.getURLs().stream().collect(Collectors.toSet()),
				garment.getTags().stream().map(t -> new TagRequestDTO(t.getId(), t.getName())).collect(Collectors.toSet()))
			).collect(Collectors.toSet())
		);
	}
}
