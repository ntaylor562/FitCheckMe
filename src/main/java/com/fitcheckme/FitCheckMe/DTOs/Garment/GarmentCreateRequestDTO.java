package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;

public record GarmentCreateRequestDTO(
	String garmentName,
	List<String> garmentURLs,
	List<TagRequestDTO> garmentTags
) {}
