package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;

public record OutfitCreateRequestDTO (
	Integer userId,
	String outfitName,
	String outfitDesc,
	List<Integer> outfitTags,
	List<GarmentCreateRequestDTO> garments,
	List<Integer> existingGarments
) {}
