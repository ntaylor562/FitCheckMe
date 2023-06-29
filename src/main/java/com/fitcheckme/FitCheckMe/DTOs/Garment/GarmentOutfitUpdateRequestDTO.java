package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

public record GarmentOutfitUpdateRequestDTO(
	Integer garmentId,
	List<Integer> addOutfitIds,
	List<Integer> removeOutfitIds
) {}
