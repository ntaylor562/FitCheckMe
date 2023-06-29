package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
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
import com.fitcheckme.FitCheckMe.services.get_services.GarmentGetService;
import com.fitcheckme.FitCheckMe.services.get_services.OutfitGetService;
import com.fitcheckme.FitCheckMe.services.get_services.TagGetService;
import com.fitcheckme.FitCheckMe.services.get_services.UserGetService;

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
	private OutfitGetService outfitGetService;

	@Autowired
	private UserGetService userGetService;

	@Autowired
	private GarmentGetService garmentGetService;

	@Autowired
	private GarmentService garmentService;

	@Autowired
	private TagGetService tagGetService;

	@Autowired
	private TagService tagService;

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
		List<Garment> existingGarments = garmentGetService.getById(outfit.existingGarments());

		//Checking if the list of garments have each garment belonging to the user whose outfit this is
		for(int i = 0; i < existingGarments.size(); ++i) {
			if(existingGarments.get(i).getUser().getId() != outfit.userId()) {
				throw new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(existingGarments.get(i).getId())));
			}
		}
		garments.addAll(existingGarments);

		List<Tag> tags = tagGetService.getById(outfit.outfitTags());

		Outfit newOutfit = new Outfit(userGetService.getById(outfit.userId()), outfit.outfitName(), outfit.outfitDesc() != "" ? outfit.outfitDesc() : null, LocalDateTime.now(), garments, tags);
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

		Outfit currentOutfit = outfitGetService.getById(outfit.outfitId());
		currentOutfit.setName(outfit.outfitName());
		currentOutfit.setDesc(outfit.outfitDesc() != "" ? outfit.outfitDesc() : null);
		
		this.outfitRepository.save(currentOutfit);
	}

	//TODO add auth so only the owner can do this. Right now it's set up to allow anyone to do this as long as the garment's user matches the outfit's user. Replace that logic with proper auth
	@Transactional
	public void editGarments(OutfitGarmentUpdateRequestDTO outfitUpdate) {
		Outfit currentOutfit = outfitGetService.getById(outfitUpdate.outfitId());
		List<Garment> addGarments = garmentGetService.getById(outfitUpdate.addGarmentIds());
		List<Garment> removeGarments = garmentGetService.getById(outfitUpdate.removeGarmentIds());

		//Checking if the list of garments have each garment belonging to the user whose outfit this is
		for(int i = 0; i < addGarments.size(); ++i) {
			if(addGarments.get(i).getUser().getId() != currentOutfit.getUser().getId()) {
				throw new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(addGarments.get(i).getId())));
			}
			garmentService.addOutfit(addGarments.get(i).getId(), currentOutfit.getId());
		}
		for(int i = 0; i < removeGarments.size(); ++i) {
			if(removeGarments.get(i).getUser().getId() != currentOutfit.getUser().getId()) {
				throw new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(removeGarments.get(i).getId())));
			}
			garmentService.removeOutfit(removeGarments.get(i).getId(), currentOutfit.getId());
		}

		currentOutfit.addGarment(addGarments);
		currentOutfit.removeGarment(removeGarments);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void editTags(OutfitTagUpdateRequestDTO outfitUpdate) {
		Outfit currentOutfit = outfitGetService.getById(outfitUpdate.outfitId());
		List<Tag> addTags = tagGetService.getById(outfitUpdate.addTagIds());
		List<Tag> removeTags = tagGetService.getById(outfitUpdate.removeTagIds());

		for(int i = 0; i < addTags.size(); ++i) {
			tagService.addOutfit(addTags.get(i).getId(), currentOutfit.getId());
		}

		for(int i = 0; i < removeTags.size(); ++i) {
			tagService.removeOutfit(removeTags.get(i).getId(), currentOutfit.getId());
		}

		currentOutfit.addTag(addTags);
		currentOutfit.removeTag(removeTags);

		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void addTag(Integer outfitId, Integer tagId) {
		Outfit currentOutfit = outfitGetService.getById(outfitId);
		Tag currentTag = tagGetService.getById(tagId);

		tagService.addOutfit(tagId, outfitId);

		currentOutfit.addTag(currentTag);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void removeTag(Integer tagId, Integer outfitId) {
		Outfit currentOutfit = outfitGetService.getById(outfitId);
		Tag currentTag = tagGetService.getById(tagId);

		tagService.removeOutfit(tagId, outfitId);

		currentOutfit.removeTag(currentTag);
		this.outfitRepository.save(currentOutfit);
	}

	//TODO implement (must update all dependent tables)
	public void deleteOutfit(Integer id) {
		
	}
}
