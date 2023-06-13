package com.fitcheckme.FitCheckMe.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.User;

public record OutfitDTO(
	Long outfitId,
	User user,
	String outfitName,
	String outfitDesc,
	LocalDateTime creationDate,
	List<Garment> garments
) {
	public static OutfitDTO fromOutfit(Outfit outfit) {
		OutfitDTO outfitDTO = new OutfitDTO(outfit.getId(), outfit.getUser(), outfit.getName(), outfit.getDesc(), outfit.getCreationDate(), outfit.getGarments());
		return outfitDTO;
	}
}
