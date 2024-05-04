package com.fitcheckme.FitCheckMe.DTOs.Tag;

import com.fitcheckme.FitCheckMe.models.Tag;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequestDTO(
	@NotBlank
	String tagName
) {
	public static TagCreateRequestDTO toDTO(Tag tag) {
		return new TagCreateRequestDTO(tag.getName());
	}
}
