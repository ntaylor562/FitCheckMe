package com.fitcheckme.FitCheckMe.DTOs.Tag;

import java.util.List;

public record TagGarmentUpdateRequestDTO(
	Integer tagId,
	List<Integer> addGarmentIds,
	List<Integer> removeGarmentIds
) {}
