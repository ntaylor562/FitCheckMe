package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record OutfitUpdateRequestDTO (
	@NotNull
	Integer outfitId,
	String outfitName,
	String outfitDesc,
	List<Integer> addGarmentIds,
	List<Integer> removeGarmentIds,
	List<Integer> addTagIds,
	List<Integer> removeTagIds
) {}
