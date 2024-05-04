package com.fitcheckme.FitCheckMe.DTOs.Tag;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record TagOutfitUpdateRequestDTO(
	@NotNull
	Integer tagId,
	List<Integer> addOutfitIds,
	List<Integer> removeOutfitIds
) {}
