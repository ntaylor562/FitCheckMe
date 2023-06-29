package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

public record GarmentURLUpdateRequestDTO(
	Integer garmentId,
	List<String> addURLs,
	List<String> removeURLs
) {}
