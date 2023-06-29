package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentOutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentTagUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentURLUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.services.get_services.GarmentGetService;
import com.fitcheckme.FitCheckMe.services.get_services.OutfitGetService;
import com.fitcheckme.FitCheckMe.services.get_services.TagGetService;
import com.fitcheckme.FitCheckMe.services.get_services.UserGetService;

import jakarta.transaction.Transactional;

@Service
public class GarmentService {
	@Value("${fitcheckme.max-garment-name-length}")
	Integer maxGarmentNameLength;

	@Value("${fitcheckme.max-urls-per-garment}")
	Integer maxURLsPerGarment;

	@Value("${fitcheckme.max-garment-url-length}")
	Integer maxGarmentURLLength;

	@Autowired
	private GarmentRepository garmentRepository;

	@Autowired
	private GarmentGetService garmentGetService;

	@Autowired
	private OutfitGetService outfitGetService;

	@Autowired
	private TagGetService tagGetService;

	@Autowired
	private TagService tagService;

	@Autowired
	private UserGetService userGetService;

	@Transactional
	public Garment createGarment(GarmentCreateRequestDTO garment) {
		if(garment.garmentName().length() > maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", maxGarmentNameLength));
		}

		if(garment.garmentURLs().size() > maxURLsPerGarment) {
			throw new IllegalArgumentException(String.format("Too many URLs provided when creating a garment, must be at most %d URLs", maxURLsPerGarment));
		}

		if(garment.garmentURLs().stream().anyMatch(url -> url.length() > maxGarmentURLLength)) {
			throw new IllegalArgumentException(String.format("Garment URL too long, must be at most %d characters", maxGarmentURLLength));
		}

		System.out.println("USER ID:");
		System.out.println(garment.userId());

		//TODO think about performing security checks on URLs
		Garment newGarment = new Garment(garment.garmentName(), userGetService.getById(garment.userId()), garment.garmentURLs(), tagGetService.getById(garment.garmentTagIds()));
		garmentRepository.save(newGarment);
		return newGarment;
	}

	@Transactional
	public List<Garment> createGarment(List<GarmentCreateRequestDTO> garments) {
		List<Garment> res = new ArrayList<Garment>();
		for(int i = 0; i < garments.size(); ++i) {
			res.add(this.createGarment(garments.get(i)));
		}
		return res;
	}

	public void updateGarment(GarmentUpdateRequestDTO garment) {
		if(garment.garmentName().length() > maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", maxGarmentNameLength));
		}

		Garment currentGarment = garmentGetService.getById(garment.garmentId());
		currentGarment.setName(garment.garmentName());

		garmentRepository.save(currentGarment);
	}

	@Transactional
	public void editOutfits(GarmentOutfitUpdateRequestDTO garmentUpdate) {
		Garment garment = garmentGetService.getById(garmentUpdate.garmentId());
		List<Outfit> addOutfits = outfitGetService.getById(garmentUpdate.addOutfitIds());
		List<Outfit> removeOutfits = outfitGetService.getById(garmentUpdate.removeOutfitIds());

		garment.addOutfit(addOutfits);
		garment.removeOutfit(removeOutfits);
		
		garmentRepository.save(garment);
	}

	public void addOutfit(Integer garmentId, Integer outfitId) {
		Garment garment = garmentGetService.getById(garmentId);
		Outfit outfit = outfitGetService.getById(outfitId);

		garment.addOutfit(outfit);
		garmentRepository.save(garment);
	}

	public void removeOutfit(Integer garmentId, Integer outfitId) {
		Garment garment = garmentGetService.getById(garmentId);
		Outfit outfit = outfitGetService.getById(outfitId);

		garment.removeOutfit(outfit);
		garmentRepository.save(garment);
	}

	@Transactional
	public void editTags(GarmentTagUpdateRequestDTO garmentUpdate) {
		Garment garment = garmentGetService.getById(garmentUpdate.garmentId());
		List<Tag> addTags = tagGetService.getById(garmentUpdate.addTagIds());
		List<Tag> removeTags = tagGetService.getById(garmentUpdate.removeTagIds());

		for(int i = 0; i < addTags.size(); ++i) {
			tagService.addGarment(addTags.get(i).getId(), garment.getId());
		}

		for(int i = 0; i < removeTags.size(); ++i) {
			tagService.removeGarment(addTags.get(i).getId(), garment.getId());
		}

		garment.addTag(addTags);
		garment.removeTag(removeTags);
		
		garmentRepository.save(garment);
	}

	@Transactional
	public void addTag(Integer garmentId, Integer tagId) {
		Garment garment = garmentGetService.getById(garmentId);
		Tag tag = tagGetService.getById(tagId);

		tagService.addGarment(tagId, garmentId);

		garment.addTag(tag);
		garmentRepository.save(garment);
	}

	@Transactional
	public void removeTag(Integer garmentId, Integer tagId) {
		Garment garment = garmentGetService.getById(garmentId);
		Tag tag = tagGetService.getById(tagId);

		tagService.removeGarment(tagId, garmentId);

		garment.removeTag(tag);
		garmentRepository.save(garment);
	}

	@Transactional
	public void editURLs(GarmentURLUpdateRequestDTO garmentUpdate) {
		Garment garment = garmentGetService.getById(garmentUpdate.garmentId());
		List<String> addURLs = garmentUpdate.addURLs();
		List<String> removeURLs = garmentUpdate.removeURLs();

		garment.addURL(addURLs);
		garment.removeURL(removeURLs);

		garmentRepository.save(garment);
	}

	public void addURL(Integer garmentId, String url) {
		Garment garment = garmentGetService.getById(garmentId);

		garment.addURL(url);
		garmentRepository.save(garment);
	}

	//TODO implement (must update all dependent tables)
	public void deleteGarment(Integer garmentId) {
		
	}
}
