package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.util.List;

public record OutfitTagUpdateRequestDTO(
	Integer outfitId,
	List<Integer> addTagIds,
	List<Integer> removeTagIds
) {}
