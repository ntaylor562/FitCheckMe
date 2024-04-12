package com.fitcheckme.FitCheckMe.DTOs.Tag;

import java.util.List;

public record TagOutfitUpdateRequestDTO(
	Integer tagId,
	List<Integer> addOutfitIds,
	List<Integer> removeOutfitIds
) {}
