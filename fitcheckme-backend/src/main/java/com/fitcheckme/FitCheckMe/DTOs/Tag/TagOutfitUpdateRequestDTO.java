package com.fitcheckme.FitCheckMe.DTOs.Tag;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record TagOutfitUpdateRequestDTO(
	@NotNull
	Integer tagId,
	Set<Integer> addOutfitIds,
	Set<Integer> removeOutfitIds
) {}
