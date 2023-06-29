package com.fitcheckme.FitCheckMe.DTOs.Garment;

import java.util.List;

public record GarmentTagUpdateRequestDTO(
	Integer garmentId,
	List<Integer> addTagIds,
	List<Integer> removeTagIds
) {}
