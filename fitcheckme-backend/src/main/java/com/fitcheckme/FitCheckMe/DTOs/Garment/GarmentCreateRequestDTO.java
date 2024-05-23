package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.Set;
import java.util.stream.Collectors;

import com.fitcheckme.FitCheckMe.models.Garment;

import jakarta.validation.constraints.NotBlank;

public record GarmentCreateRequestDTO(
		@NotBlank String garmentName,
		Set<String> garmentURLs,
		Set<Integer> garmentTags) {

	public static GarmentCreateRequestDTO toDTO(Garment garment) {
		return new GarmentCreateRequestDTO(garment.getName(), garment.getURLs(),
				garment.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet()));
	}
}
