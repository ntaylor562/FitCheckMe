package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import com.fitcheckme.FitCheckMe.models.Garment;

public record GarmentCreateRequestDTO(
	String garmentName,
	List<String> garmentURLs,
	List<Integer> garmentTagIds
) {

	public static GarmentCreateRequestDTO toDTO(Garment garment) {
		return new GarmentCreateRequestDTO(garment.getName(), garment.getURLs().stream().toList(), garment.getTags().stream().map(tag -> tag.getId()).toList());
	}
}
