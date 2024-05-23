package com.fitcheckme.FitCheckMe.DTOs.Tag;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record TagGarmentUpdateRequestDTO(
	@NotNull
	Integer tagId,
	Set<Integer> addGarmentIds,
	Set<Integer> removeGarmentIds
) {}
