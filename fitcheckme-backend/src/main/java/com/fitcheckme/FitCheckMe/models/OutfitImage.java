package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "outfit_image", schema = "app")
public class OutfitImage extends ImageFile {

	@ManyToOne
	@JoinColumn(name = "outfit_id", nullable = false)
	private Outfit outfit;

	public OutfitImage() {

	}

	public OutfitImage(String imagePath, LocalDateTime creationDate, Outfit outfit) {
		super(imagePath, creationDate);
		this.outfit = outfit;
	}

	public Outfit getOutfit() {
		return outfit;
	}
}
