package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentTagUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentURLUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

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

	private final GarmentRepository garmentRepository;
	private final TagRepository tagRepository;
	private final UserService userService;
	private final UserRepository userRepository;

	public GarmentService(GarmentRepository garmentRepository, TagRepository tagRepository, UserService userService, UserRepository userRepository) {
		this.garmentRepository = garmentRepository;
		this.tagRepository = tagRepository;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	public List<GarmentRequestDTO> getAll() {
		return garmentRepository.findAllByOrderByIdAsc().stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}

	public GarmentRequestDTO getById(Integer id) {
		return GarmentRequestDTO.toDTO(garmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(id)))));
	}

	private Garment getGarment(Integer id) {
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

	public boolean exists(Integer id) {
		return garmentRepository.existsById(id);
	}

	public List<GarmentRequestDTO> getUserGarments(Integer userId) {
		//Checking the user exists
		if(!this.userService.exists(userId)) {
			throw new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(userId)));
		}
		return this.garmentRepository.findByUserId(userId).stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
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
	public GarmentRequestDTO createGarment(GarmentCreateRequestDTO garment) {
		if(garment.garmentName().length() > maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", maxGarmentNameLength));
		}

		if(garment.garmentURLs().size() > maxURLsPerGarment) {
			throw new IllegalArgumentException(String.format("Too many URLs provided when creating a garment, must be at most %d URLs", maxURLsPerGarment));
		}

		if(garment.garmentURLs().stream().anyMatch(url -> url.length() > maxGarmentURLLength)) {
			throw new IllegalArgumentException(String.format("Garment URL too long, must be at most %d characters", maxGarmentURLLength));
		}

		List<Tag> tags = new ArrayList<Tag>();
		for(int id : garment.garmentTagIds()) {
			tags.add(tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id)))));
		}

		//TODO think about performing security checks on URLs
		Garment newGarment = new Garment(garment.garmentName(), userRepository.findById(garment.userId()).get(), garment.garmentURLs(), tags);
		garmentRepository.save(newGarment);
		return GarmentRequestDTO.toDTO(newGarment);
	}

	@Transactional
	public List<GarmentRequestDTO> createGarment(List<GarmentCreateRequestDTO> garments) {
		List<GarmentRequestDTO> res = new ArrayList<GarmentRequestDTO>();
		for(int i = 0; i < garments.size(); ++i) {
			res.add(this.createGarment(garments.get(i)));
		}
		return res;
	}

	public GarmentRequestDTO updateGarment(GarmentUpdateRequestDTO garment) {
		if(garment.garmentName().length() > maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", maxGarmentNameLength));
		}

		Garment currentGarment = this.getGarment(garment.garmentId());
		currentGarment.setName(garment.garmentName());

		garmentRepository.save(currentGarment);
		return GarmentRequestDTO.toDTO(currentGarment);
	}

	@Transactional
	public void editTags(GarmentTagUpdateRequestDTO garmentUpdate) {
		Garment garment = this.getGarment(garmentUpdate.garmentId());
		List<Tag> addTags = new ArrayList<Tag>();
		List<Tag> removeTags = new ArrayList<Tag>();

		for (int id : garmentUpdate.addTagIds()) {
			addTags.add(tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id)))));
		}
		for (int id : garmentUpdate.removeTagIds()) {
			removeTags.add(tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id)))));
		}

		garment.addTag(addTags);
		garment.removeTag(removeTags);
		
		garmentRepository.save(garment);
	}

	@Transactional
	public void addTag(Integer garmentId, Integer tagId) {
		Garment garment = this.getGarment(garmentId);
		Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(tagId))));

		garment.addTag(tag);
		garmentRepository.save(garment);
	}

	@Transactional
	public void removeTag(Integer garmentId, Integer tagId) {
		Garment garment = this.getGarment(garmentId);
		Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(tagId))));

		garment.removeTag(tag);
		garmentRepository.save(garment);
	}

	@Transactional
	public void editURLs(GarmentURLUpdateRequestDTO garmentUpdate) {
		Garment garment = this.getGarment(garmentUpdate.garmentId());
		List<String> addURLs = garmentUpdate.addURLs();
		List<String> removeURLs = garmentUpdate.removeURLs();

		validateURLInput(garment, addURLs, removeURLs);

		garment.addURL(addURLs);
		garment.removeURL(removeURLs);

		garmentRepository.save(garment);
	}

	public void addURL(Integer garmentId, String url) {
		Garment garment = this.getGarment(garmentId);

		validateURLInput(garment, new ArrayList<String>(List.of(url)), new ArrayList<String>());

		garment.addURL(url);
		garmentRepository.save(garment);
	}

	public void removeURL(Integer garmentId, String url) {
		Garment garment = this.getGarment(garmentId);


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