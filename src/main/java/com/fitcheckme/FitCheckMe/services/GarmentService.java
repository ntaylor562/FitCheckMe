package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentTagUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentURLUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class GarmentService {
	@Value("${fitcheckme.max-garment-name-length}")
	private Integer maxGarmentNameLength;

	@Value("${fitcheckme.max-urls-per-garment}")
	private Integer maxURLsPerGarment;

	@Value("${fitcheckme.max-garment-url-length}")
	private Integer maxGarmentURLLength;

	@Autowired
	private GarmentRepository garmentRepository;

	@Autowired
	private TagService tagService;

	@Autowired
	private UserService userService;


	public List<Garment> getAll() {
		return garmentRepository.findAll();
	}

	public Garment getById(Integer id) {
		return garmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(id))));
	}

	public List<Garment> getById(List<Integer> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<Garment>();
		}

		List<Garment> res = garmentRepository.findAllById(ids);

		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			List<Integer> missingIds = res.stream().filter(g -> !ids.contains(g.getId())).map(Garment::getId).collect(Collectors.toList());
			throw new EntityNotFoundException(String.format("Garments with the following ids not found: %s", missingIds.toString()));
		}

		return garmentRepository.findAllById(ids);
	}

	
	/**
	 * Validates the input URLs for a garment. If the number of URLs exceeds the maximum limit or if a URL is too long, an exception is thrown.
	 * 
	 * @param garment The garment object.
	 * @param addURLs The list of URLs to be added.
	 * @param removeURLs The list of URLs to be removed.
	 * @throws IllegalArgumentException If the number of URLs exceeds the maximum limit or if a URL is too long.
	 */
	private void validateURLInput(Garment garment, List<String> addURLs, List<String> removeURLs) {
		if(addURLs.size() + garment.getURLs().size() - removeURLs.size() > maxURLsPerGarment) {
			throw new IllegalArgumentException(String.format("Too many URLs provided when creating a garment, must be at most %d URLs", maxURLsPerGarment));
		}

		for(int i = 0; i < addURLs.size(); ++i) {
			if(addURLs.get(i).length() > maxGarmentURLLength) {
				throw new IllegalArgumentException(String.format("Garment URL %s too long, must be at most %d characters", addURLs.get(i), maxGarmentURLLength));
			}
		}
	}

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
		Garment newGarment = new Garment(garment.garmentName(), userService.getById(garment.userId()), garment.garmentURLs(), tagService.getById(garment.garmentTagIds()));
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

		Garment currentGarment = this.getById(garment.garmentId());
		currentGarment.setName(garment.garmentName());

		garmentRepository.save(currentGarment);
	}

	@Transactional
	public void editTags(GarmentTagUpdateRequestDTO garmentUpdate) {
		Garment garment = this.getById(garmentUpdate.garmentId());
		List<Tag> addTags = tagService.getById(garmentUpdate.addTagIds());
		List<Tag> removeTags = tagService.getById(garmentUpdate.removeTagIds());

		garment.addTag(addTags);
		garment.removeTag(removeTags);
		
		garmentRepository.save(garment);
	}

	@Transactional
	public void addTag(Integer garmentId, Integer tagId) {
		Garment garment = this.getById(garmentId);
		Tag tag = tagService.getById(tagId);

		garment.addTag(tag);
		garmentRepository.save(garment);
	}

	@Transactional
	public void removeTag(Integer garmentId, Integer tagId) {
		Garment garment = this.getById(garmentId);
		Tag tag = tagService.getById(tagId);

		garment.removeTag(tag);
		garmentRepository.save(garment);
	}

	@Transactional
	public void editURLs(GarmentURLUpdateRequestDTO garmentUpdate) {
		Garment garment = this.getById(garmentUpdate.garmentId());
		List<String> addURLs = garmentUpdate.addURLs();
		List<String> removeURLs = garmentUpdate.removeURLs();

		validateURLInput(garment, addURLs, removeURLs);

		garment.addURL(addURLs);
		garment.removeURL(removeURLs);

		garmentRepository.save(garment);
	}

	public void addURL(Integer garmentId, String url) {
		Garment garment = this.getById(garmentId);

		validateURLInput(garment, new ArrayList<String>(List.of(url)), new ArrayList<String>());

		garment.addURL(url);
		garmentRepository.save(garment);
	}

	public void removeURL(Integer garmentId, String url) {
		Garment garment = this.getById(garmentId);


		if(!garment.getURLs().contains(url)) {
			throw new IllegalArgumentException(String.format("Garment does not contain URL %s", url));
		}

		validateURLInput(garment, new ArrayList<String>(), new ArrayList<String>(List.of(url)));

		garment.removeURL(url);
		garmentRepository.save(garment);
	}

	//TODO implement (must update all dependent tables)
	public void deleteGarment(Integer garmentId) {
		
	}
}
