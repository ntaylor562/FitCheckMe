package com.fitcheckme.FitCheckMe.models.composite_keys;

import java.io.Serializable;

import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.ImageFile;

public class GarmentImageId implements Serializable {
	private ImageFile image;
	private Garment garment;

	public GarmentImageId() {

	}

	public GarmentImageId(ImageFile image, Garment garment) {
		this.image = image;
		this.garment = garment;
	}

	public ImageFile getImage() {
		return image;
	}

	public Garment getGarment() {
		return garment;
	}
}
