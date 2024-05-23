package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record OutfitUpdateRequestDTO (
	@NotNull
	Integer outfitId,
	String outfitName,
	String outfitDesc,
	Set<Integer> addGarmentIds,
	Set<Integer> removeGarmentIds,
	Set<Integer> addTagIds,
	Set<Integer> removeTagIds
) {}
