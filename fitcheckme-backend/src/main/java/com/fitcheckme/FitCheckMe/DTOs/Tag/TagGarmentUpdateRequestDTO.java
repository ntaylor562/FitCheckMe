package com.fitcheckme.FitCheckMe.DTOs.Tag;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record TagGarmentUpdateRequestDTO(
	@NotNull
	Integer tagId,
	List<Integer> addGarmentIds,
	List<Integer> removeGarmentIds
) {}
