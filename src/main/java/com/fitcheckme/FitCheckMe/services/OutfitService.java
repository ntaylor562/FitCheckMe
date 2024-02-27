package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitGarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitTagUpdateRequestDTO;
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

	public List<Outfit> getById(List<Integer> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<Outfit>();
		}
		
		List<Outfit> res = outfitRepository.findAllById(ids);
		
		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			throw new EntityNotFoundException(String.format("%d/%d outfits in list not found", ids.size() - res.size(), ids.size()));
		}

		return res;
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

		Outfit newOutfit = new Outfit(userService.getById(outfit.userId()), outfit.outfitName(), outfit.outfitDesc() != "" ? outfit.outfitDesc() : null, LocalDateTime.now(), garments, tags);
		this.outfitRepository.save(newOutfit);
		return newOutfit;
	}

	public void updateOutfit(OutfitUpdateRequestDTO outfit) {
		if(outfit.outfitName().length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		if(outfit.outfitDesc().length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
		}

		Outfit currentOutfit = this.getById(outfit.outfitId());
		currentOutfit.setName(outfit.outfitName());
		currentOutfit.setDesc(outfit.outfitDesc() != "" ? outfit.outfitDesc() : null);
		
		this.outfitRepository.save(currentOutfit);
	}

	//TODO add auth so only the owner can do this. Right now it's set up to allow anyone to do this as long as the garment's user matches the outfit's user. Replace that logic with proper auth
	@Transactional
	public void editGarments(OutfitGarmentUpdateRequestDTO outfitUpdate) {
		Outfit currentOutfit = this.getById(outfitUpdate.outfitId());
		List<Garment> addGarments = garmentService.getById(outfitUpdate.addGarmentIds());
		List<Garment> removeGarments = garmentService.getById(outfitUpdate.removeGarmentIds());

		//Checking if the list of garments have each garment belonging to the user whose outfit this is
		for(int i = 0; i < addGarments.size(); ++i) {
			if(addGarments.get(i).getUser().getId() != currentOutfit.getUser().getId()) {
				throw new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(addGarments.get(i).getId())));
			}
		}
		for(int i = 0; i < removeGarments.size(); ++i) {
			if(removeGarments.get(i).getUser().getId() != currentOutfit.getUser().getId()) {
				throw new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(removeGarments.get(i).getId())));
			}
		}

		currentOutfit.addGarment(addGarments);
		currentOutfit.removeGarment(removeGarments);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void editTags(OutfitTagUpdateRequestDTO outfitUpdate) {
		Outfit currentOutfit = this.getById(outfitUpdate.outfitId());
		List<Tag> addTags = tagService.getById(outfitUpdate.addTagIds());
		List<Tag> removeTags = tagService.getById(outfitUpdate.removeTagIds());

		currentOutfit.addTag(addTags);
		currentOutfit.removeTag(removeTags);

		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void addTag(Integer outfitId, Integer tagId) {
		Outfit currentOutfit = this.getById(outfitId);
		Tag currentTag = tagService.getById(tagId);

		currentOutfit.addTag(currentTag);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void removeTag(Integer tagId, Integer outfitId) {
		Outfit currentOutfit = this.getById(outfitId);
		Tag currentTag = tagService.getById(tagId);

		currentOutfit.removeTag(currentTag);
		this.outfitRepository.save(currentOutfit);
	}

	//TODO implement (must update all dependent tables)
	public void deleteOutfit(Integer id) {
		
	}
}
