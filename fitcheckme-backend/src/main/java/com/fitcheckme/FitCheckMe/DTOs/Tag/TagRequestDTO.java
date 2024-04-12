package com.fitcheckme.FitCheckMe.DTOs.Tag;

import com.fitcheckme.FitCheckMe.models.Tag;

public record TagRequestDTO(
	int tagId,
	String tagName
) {
	public static TagRequestDTO toDTO(Tag tag) {
		return new TagRequestDTO(tag.getId(), tag.getName());
	}
}
