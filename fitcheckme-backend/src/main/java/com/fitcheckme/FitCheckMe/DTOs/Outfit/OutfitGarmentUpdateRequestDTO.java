package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

public record OutfitGarmentUpdateRequestDTO(
	Integer outfitId,
	List<Integer> addGarmentIds,
	List<Integer> removeGarmentIds
) {}
