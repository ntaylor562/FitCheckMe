package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

public record GarmentCreateRequestDTO(
	String garmentName,
	List<String> garmentURLs,
	List<Integer> garmentTagIds
) {}
