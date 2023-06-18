package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;

import jakarta.persistence.EntityNotFoundException;

//TODO implement auth permissions for this service
@Service
public class OutfitService {
	@Autowired
	private OutfitRepository outfitRepository;

	@Autowired
	private UserService userService;

	public List<Outfit> getAll() {
		return outfitRepository.findAll();
	}

	public Outfit getById(Long id) {
		return outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id))));
	}

	public void createOutfit(OutfitCreateRequestDTO outfit) {
		this.outfitRepository.save(new Outfit(userService.getById(outfit.userId()), outfit.outfitName(), outfit.outfitDesc(), LocalDateTime.now()));
	}

	public void updateOutfit(OutfitUpdateRequestDTO outfit) {
		Outfit currentOutfit = outfitRepository.findById(outfit.outfitId()).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(outfit.outfitId()))));
		currentOutfit.setName(outfit.outfitName());
		currentOutfit.setDesc(outfit.outfitDesc());
		//TODO think out how to edit garments in an outfit
		this.outfitRepository.save(currentOutfit);
	}

	public void deleteOutfit(Long id) {
		outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id))));
		this.outfitRepository.deleteById(id);
	}
}
