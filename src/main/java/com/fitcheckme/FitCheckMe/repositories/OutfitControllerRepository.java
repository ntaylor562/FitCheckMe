package com.fitcheckme.FitCheckMe.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fitcheckme.FitCheckMe.models.ClothesTag;
import com.fitcheckme.FitCheckMe.models.ClothingItem;
import com.fitcheckme.FitCheckMe.models.Outfit;

import jakarta.annotation.PostConstruct;

@Repository
public class OutfitControllerRepository {
	public final List<Outfit> outfits = new ArrayList<Outfit>();

	public OutfitControllerRepository() {

	}

	public List<Outfit> findAll() {
		return this.outfits;
	}

	public Optional<Outfit> findById(Integer id) {
		return this.outfits.stream().filter(o -> o.id().equals(id)).findFirst();
	}

	public void createOutfit(Outfit outfit) {
		this.outfits.add(outfit);
	}

	public void removeOutfit(Integer id) {
		this.outfits.removeIf(o -> o.id().equals(id));
	}

	public void updateOutfit(Outfit outfit) {
		this.removeOutfit(outfit.id());
		this.createOutfit(outfit);
	}


	@PostConstruct
	private void init() {
		Outfit fit1 = new Outfit(
			1, 
			new ArrayList<ClothingItem>(), 
			"Outfit 1", 
			LocalDateTime.now(), 
			LocalDateTime.now(), 
			new ArrayList<ClothesTag>()
		);

		this.outfits.add(fit1);
	}
}
