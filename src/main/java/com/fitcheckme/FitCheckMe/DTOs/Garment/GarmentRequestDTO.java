package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;

public record GarmentRequestDTO(
	Long garmentId,
	String garmentName,
	
	List<Long> outfitIds,
	List<String> urls,
	List<TagRequestDTO> garmentTags
) {}
