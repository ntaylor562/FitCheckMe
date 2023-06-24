package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

public record GarmentCreateRequestDTO(
	String garmentName,
	Integer userId,
	List<String> garmentURLs,
	List<Integer> garmentTagIds
) {}
