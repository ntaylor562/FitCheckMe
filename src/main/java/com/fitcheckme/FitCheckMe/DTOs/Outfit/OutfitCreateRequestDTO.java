package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;

public record OutfitCreateRequestDTO (
	Long userId,
	String outfitName,
	String outfitDesc,
	List<GarmentCreateRequestDTO> garments
) {}
