package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "garment_image", schema = "app")
public class GarmentImage extends ImageFile {

	@ManyToOne
	@JoinColumn(name = "garment_id", nullable = false)
	private Garment garment;

	public GarmentImage() {

	}

	public GarmentImage(String imagePath, LocalDateTime creationDate, Garment garment) {
		super(imagePath, creationDate);
		this.garment = garment;
	}

	public Garment getGarment() {
		return garment;
	}
}
