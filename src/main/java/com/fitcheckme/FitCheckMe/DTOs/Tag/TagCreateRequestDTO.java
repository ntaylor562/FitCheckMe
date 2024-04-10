package com.fitcheckme.FitCheckMe.DTOs.Tag;

import com.fitcheckme.FitCheckMe.models.Tag;

public record TagCreateRequestDTO(
	String tagName
) {
	public static TagCreateRequestDTO toDTO(Tag tag) {
		return new TagCreateRequestDTO(tag.getName());
	}
}
