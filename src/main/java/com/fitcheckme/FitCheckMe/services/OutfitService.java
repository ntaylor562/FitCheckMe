package com.fitcheckme.FitCheckMe.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;

//TODO implement permissions for this service
@Service
public class OutfitService {
	@Autowired
	private OutfitRepository outfitRepository;

	public List<Outfit> getAll() {
		return outfitRepository.findAll();
	}

	public Optional<Outfit> getOutfitById(Long id) {
		return Optional.of(this.outfitRepository.getReferenceById(id));
	}

	public void createOutfit(Outfit outfit) {
		this.outfitRepository.save(outfit);
	}

	public void updateOutfit(Outfit outfit) {
		this.outfitRepository.save(outfit);
	}

	public void deleteOutfit(Long id) {
		this.outfitRepository.deleteById(id);
	}
}
