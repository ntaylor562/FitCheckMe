package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record OutfitTagUpdateRequestDTO(
	@NotNull
	Integer outfitId,
	List<Integer> addTagIds,
	List<Integer> removeTagIds
) {}
