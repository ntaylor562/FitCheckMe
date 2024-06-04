package com.fitcheckme.FitCheckMe.models;

import com.fitcheckme.FitCheckMe.models.composite_keys.OutfitImageId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "outfit_image", schema = "app")
@IdClass(OutfitImageId.class)
public class OutfitImage {
	@Id
	@ManyToOne
	@JoinColumn(name = "image_id", nullable = false)
	private ImageFile image;

	@Id
	@ManyToOne
	@JoinColumn(name = "outfit_id", nullable = false)
	private Outfit outfit;

	public OutfitImage() {

	}

	public OutfitImage(ImageFile image, Outfit outfit) {
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
