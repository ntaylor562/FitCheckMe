package com.fitcheckme.FitCheckMe.DTOs.files;

import com.fitcheckme.FitCheckMe.models.ImageFile;

public record ImageRequestDTO(
	Integer userId,
	Integer fileId,
	String fileName
) {
	public static ImageRequestDTO toDTO(ImageFile image) {
		if(image == null)
			return null;
		return new ImageRequestDTO(image.getUser().getId(), image.getId(), image.getImagePath());
	}
}
