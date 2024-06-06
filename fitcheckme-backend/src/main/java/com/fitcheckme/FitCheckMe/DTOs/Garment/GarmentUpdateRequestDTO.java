package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public record GarmentUpdateRequestDTO(
	@NotNull
	Integer garmentId,
	String garmentName,
	Set<String> addURLs,
	Set<String> removeURLs,
	Set<Integer> addTagIds,
	Set<Integer> removeTagIds
) {}
