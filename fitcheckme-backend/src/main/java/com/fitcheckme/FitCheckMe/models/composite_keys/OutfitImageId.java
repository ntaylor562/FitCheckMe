package com.fitcheckme.FitCheckMe.models.composite_keys;

import java.io.Serializable;

import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.ImageFile;

public class OutfitImageId implements Serializable {
	private ImageFile image;
	private Outfit outfit;

	public OutfitImageId() {

	}

	public OutfitImageId(ImageFile image, Outfit outfit) {
		this.image = image;
		this.outfit = outfit;
	}

	public ImageFile getImage() {
		return image;
	}

	public Outfit getOutfit() {
		return outfit;
	}
}
