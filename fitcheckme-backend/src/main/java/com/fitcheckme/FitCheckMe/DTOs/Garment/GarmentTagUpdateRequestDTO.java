package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record GarmentTagUpdateRequestDTO(
	@NotNull
	Integer garmentId,
	Set<Integer> addTagIds,
	Set<Integer> removeTagIds
) {}
