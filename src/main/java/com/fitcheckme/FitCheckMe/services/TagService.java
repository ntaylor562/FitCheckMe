package com.fitcheckme.FitCheckMe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagGarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagOutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.services.get_services.GarmentGetService;
import com.fitcheckme.FitCheckMe.services.get_services.OutfitGetService;
import com.fitcheckme.FitCheckMe.services.get_services.TagGetService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TagService {
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private TagGetService tagGetService;

	@Autowired
	private OutfitGetService outfitGetService;

	@Autowired
	private GarmentGetService garmentGetService;

	//TODO add auth so only admins can create tags
	public Tag createTag(TagCreateRequestDTO tag) {
		if(tagRepository.existsByTagNameIgnoreCase(tag.tagName())) {
			throw new DataIntegrityViolationException(String.format("Tagname '%s' is already used", tag.tagName()));
		}
		Tag newTag = new Tag(tag.tagName().toLowerCase());
		this.tagRepository.save(newTag);
		return newTag;
	}

	public void editOutfits(TagOutfitUpdateRequestDTO tagUpdate) {
		Tag tag = tagGetService.getById(tagUpdate.tagId());
		List<Outfit> addOutfits = outfitGetService.getById(tagUpdate.addOutfitIds());
		List<Outfit> removeOutfits = outfitGetService.getById(tagUpdate.removeOutfitIds());

		tag.addOutfit(addOutfits);
		tag.removeOutfit(removeOutfits);
		tagRepository.save(tag);
	}

	public void addOutfit(Integer tagId, Integer outfitId) {
		Tag tag = tagGetService.getById(tagId);
		Outfit outfit = outfitGetService.getById(outfitId);

		tag.addOutfit(outfit);
		tagRepository.save(tag);
	}

	public void removeOutfit(Integer tagId, Integer outfitId) {
		Tag tag = tagGetService.getById(tagId);
		Outfit outfit = outfitGetService.getById(outfitId);

		tag.removeOutfit(outfit);
		tagRepository.save(tag);
	}

	public void editGarments(TagGarmentUpdateRequestDTO tagUpdate) {
		Tag tag = tagGetService.getById(tagUpdate.tagId());
		List<Garment> addGarments = garmentGetService.getById(tagUpdate.addGarmentIds());
		List<Garment> removeGarments = garmentGetService.getById(tagUpdate.removeGarmentIds());

		tag.addGarment(addGarments);
		tag.removeGarment(removeGarments);
		tagRepository.save(tag);
	}

	public void addGarment(Integer tagId, Integer garmentId) {
		Tag tag = tagGetService.getById(tagId);
		Garment garment = garmentGetService.getById(garmentId);

		tag.addGarment(garment);
		tagRepository.save(tag);
	}

	public void removeGarment(Integer tagId, Integer garmentId) {
		Tag tag = tagGetService.getById(tagId);
		Garment garment = garmentGetService.getById(garmentId);

		tag.removeGarment(garment);
		tagRepository.save(tag);
	}

	public Tag getByTagName(String tagName) {
		return this.tagRepository.findByTagNameIgnoreCase(tagName).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with name: %s", String.valueOf(tagName))));
	}
}
