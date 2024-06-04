package com.fitcheckme.FitCheckMe.models;

import com.fitcheckme.FitCheckMe.models.composite_keys.GarmentImageId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "garment_image", schema = "app")
@IdClass(GarmentImageId.class)
public class GarmentImage {
	@Id
	@ManyToOne
	@JoinColumn(name = "image_id", nullable = false)
	private ImageFile image;

	@Id
	@ManyToOne
	@JoinColumn(name = "garment_id", nullable = false)
	private Garment garment;

	public GarmentImage() {

	}

	public GarmentImage(ImageFile image, Garment garment) {
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
