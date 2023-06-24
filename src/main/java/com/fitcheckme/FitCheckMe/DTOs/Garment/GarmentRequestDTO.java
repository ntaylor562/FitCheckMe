package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;

public record GarmentRequestDTO(
	Integer garmentId,
	String garmentName,
	
	Integer userId,
	List<Integer> outfitIds,
	List<String> urls,
	List<TagRequestDTO> garmentTags
) {}
