package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

//TODO implement auth permissions for this service
@Service
public class OutfitService {
	@Value("${fitcheckme.max-outfit-desc-length}")
	private int maxDescLength;

	@Value("${fitcheckme.max-outfit-name-length}")
	private int maxNameLength;
	
	@Autowired
	private OutfitRepository outfitRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private TagService tagService;

	public List<Outfit> getAll() {
		return outfitRepository.findAll();
	}

	public Outfit getById(Integer id) {
		return outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id))));
	}

	//TODO add auth
	@Transactional
	public Outfit createOutfit(OutfitCreateRequestDTO outfit) {
		if(outfit.outfitName().length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		if(outfit.outfitDesc().length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
		}
		List<Garment> garments = garmentService.createGarment(outfit.garments());
		List<Garment> existingGarments = garmentService.getById(outfit.existingGarments());

		//Checking if the list of garments have each garment belonging to the user whose outfit this is
		for(int i = 0; i < existingGarments.size(); ++i) {
			if(existingGarments.get(i).getUser().getId() != outfit.userId()) {
				throw new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(existingGarments.get(i).getId())));
			}
		}
		garments.addAll(existingGarments);

		List<Tag> tags = tagService.getById(outfit.outfitTags());

		Outfit newOutfit = new Outfit(userService.getById(outfit.userId()), outfit.outfitName(), outfit.outfitDesc(), LocalDateTime.now(), garments, tags);
		this.outfitRepository.save(newOutfit);
		System.out.println("NEW OUTFIT'S USER'S OUTFITS:");
		System.out.println(newOutfit.getUser().getOutfits());
		return newOutfit;
	}

	public void updateOutfit(OutfitUpdateRequestDTO outfit) {
		if(outfit.outfitName().length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		if(outfit.outfitDesc().length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
		}

		Outfit currentOutfit = outfitRepository.findById(outfit.outfitId()).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(outfit.outfitId()))));
		currentOutfit.setName(outfit.outfitName());
		currentOutfit.setDesc(outfit.outfitDesc());
		//TODO think out how to edit garments and tags in an outfit (maybe some add/remove methods)
		this.outfitRepository.save(currentOutfit);
	}

	//TODO add auth
	public void deleteOutfit(Integer id) {
		outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id))));
		this.outfitRepository.deleteById(id);
	}
}
