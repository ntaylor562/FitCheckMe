package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record Outfit (
	@NotBlank
	Integer id,
	List<ClothingItem> clothingItems,
	@NotBlank
	String desc,
	@NotBlank
	LocalDateTime dateCreated,
	@NotBlank
	LocalDateTime dateModified,
	List<ClothesTag> tags
) {}
