package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.Set;
import java.util.stream.Collectors;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;

public record GarmentRequestDTO(
	Integer garmentId,
	String garmentName,
	
	Integer userId,
	Set<String> urls,
	Set<TagRequestDTO> garmentTags
) {
	public static GarmentRequestDTO toDTO(Garment garment) {
		return new GarmentRequestDTO(
			garment.getId(),
			garment.getName(),
			garment.getUser().getId(),
			garment.getURLs().stream().collect(Collectors.toSet()),
			garment.getTags().stream().map(tag -> TagRequestDTO.toDTO(tag)).collect(Collectors.toSet())
		);
	}
}
