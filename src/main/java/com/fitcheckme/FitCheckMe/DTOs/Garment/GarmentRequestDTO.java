package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;

public record GarmentRequestDTO(
	Integer garmentId,
	String garmentName,
	
	Integer userId,
	List<String> urls,
	List<TagRequestDTO> garmentTags
) {
	public static GarmentRequestDTO toDTO(Garment garment) {
		return new GarmentRequestDTO(
			garment.getId(),
			garment.getName(),
			garment.getUser().getId(),
			garment.getURLs(),
			garment.getTags().stream().map(tag -> TagRequestDTO.toDTO(tag)).toList()
		);
	}
}
