package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record GarmentTagUpdateRequestDTO(
	@NotNull
	Integer garmentId,
	List<Integer> addTagIds,
	List<Integer> removeTagIds
) {}
