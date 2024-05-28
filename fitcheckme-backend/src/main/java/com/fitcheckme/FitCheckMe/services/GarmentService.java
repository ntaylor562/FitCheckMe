package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentTagUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentURLUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
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

	@Value("${fitcheckme.max-garment-tags}")
	private Integer maxTagsPerGarment;

	private final GarmentRepository garmentRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;

	public GarmentService(GarmentRepository garmentRepository, TagRepository tagRepository, UserRepository userRepository) {
		this.garmentRepository = garmentRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GARMENT_ADMIN')")
	public List<GarmentRequestDTO> getAll() {
		return garmentRepository.findAllByOrderByIdAsc().stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}

	public GarmentRequestDTO getById(Integer id) throws EntityNotFoundException {
		return GarmentRequestDTO.toDTO(garmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(id)))));
	}

	@Transactional
	private Garment getGarment(Integer id) throws EntityNotFoundException {
		return garmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(id))));
	}

	public List<GarmentRequestDTO> getById(List<Integer> ids) throws EntityNotFoundException {
		if(ids.isEmpty()) {
			return new ArrayList<GarmentRequestDTO>();
		}

		List<Garment> res = garmentRepository.findAllById(ids);

		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			List<Integer> missingIds = ids.stream().filter(id -> res.stream().noneMatch(garment -> garment.getId().equals(id))).toList();
			throw new EntityNotFoundException(String.format("Garments with the following IDs not found: %s", missingIds.toString()));
		}

		return res.stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}

	public boolean exists(Integer id) {
		return garmentRepository.existsById(id);
	}

	public List<GarmentRequestDTO> getUserGarments(Integer userId) throws EntityNotFoundException {
		//Checking the user exists
		if(!this.userRepository.existsById(userId)) {
			throw new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(userId)));
		}
		return this.garmentRepository.findByUserId(userId).stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}

	public List<GarmentRequestDTO> getUserGarments(String username) throws EntityNotFoundException {
		//Checking the user exists
		if(!this.userRepository.existsByUsernameIgnoreCase(username)) {
			throw new EntityNotFoundException(String.format("User not found with username: %s", username));
		}
		return this.garmentRepository.findByUser_UsernameIgnoreCase(username).stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}
	
	/**
	 * Validates the input URLs for a garment. If the number of URLs exceeds the maximum limit or if a URL is too long, an exception is thrown.
	 * 
	 * @param garment The garment object.
	 * @param addURLs The list of URLs to be added.
	 * @param removeURLs The list of URLs to be removed.
	 * @throws IllegalArgumentException If the number of URLs exceeds the maximum limit or if a URL is too long.
	 */
	private void validateURLInput(Garment garment, Set<String> addURLs, Set<String> removeURLs) throws IllegalArgumentException {
		if(addURLs.size() + garment.getURLs().size() - removeURLs.size() > this.maxURLsPerGarment) {
			throw new IllegalArgumentException(String.format("Too many URLs provided when creating a garment, must be at most %d URLs", this.maxURLsPerGarment));
		}

		for(String url : addURLs) {
			if(url.length() > maxGarmentURLLength) {
				throw new IllegalArgumentException(String.format("Garment URL %s too long, must be at most %d characters", url, this.maxGarmentURLLength));
			}
		}
	}

	@Transactional
	private GarmentRequestDTO createGarment(GarmentCreateRequestDTO garment, User user) throws EntityNotFoundException, IllegalArgumentException {
		String garmentName = garment.garmentName() != null ? garment.garmentName().strip() : "";
		Set<String> garmentURLs = new HashSet<>(garment.garmentURLs().stream().map(url -> url.strip()).toList());
		Set<Tag> tags = new HashSet<>();

		if(garmentName.length() > this.maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", this.maxGarmentNameLength));
		}

		if(garmentURLs != null) {
			if(garmentURLs.size() > this.maxURLsPerGarment) {
				throw new IllegalArgumentException(String.format("Too many URLs provided when creating a garment, must be at most %d URLs", this.maxURLsPerGarment));
			}
			if(garmentURLs.stream().anyMatch(url -> url.length() > this.maxGarmentURLLength)) {
				throw new IllegalArgumentException(String.format("Garment URL too long, must be at most %d characters", this.maxGarmentURLLength));
			}
		}

		if(garment.garmentTags() != null && !garment.garmentTags().isEmpty()) {
			if(garment.garmentTags().size() > this.maxTagsPerGarment) {
				throw new IllegalArgumentException(String.format("Too many tags provided when creating a garment, must be at most %d tags", this.maxTagsPerGarment));
			}

			List<Tag> foundTags = tagRepository.findAllById(garment.garmentTags());
			if (foundTags.size() != garment.garmentTags().size()) {
				List<Integer> missingIds = garment.garmentTags().stream()
						.filter(id -> foundTags.stream().noneMatch(tag -> tag.getId().equals(id)))
						.toList();
				throw new EntityNotFoundException(String.format("Tags with the following IDs not found: %s", missingIds.toString()));
			}
		}
		
		//TODO think about performing security checks on URLs
		Garment newGarment = new Garment(user, garment.garmentName(), garmentURLs, tags);
		return GarmentRequestDTO.toDTO(garmentRepository.save(newGarment));
	}

	@Transactional
	public GarmentRequestDTO createGarment(GarmentCreateRequestDTO garment, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with username: %s", userDetails.getUsername())));
		return this.createGarment(garment, user);
	}

	@Transactional
	public List<GarmentRequestDTO> createGarment(List<GarmentCreateRequestDTO> garments, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException{
		User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException(String.format("User '%s' not found", userDetails.getUsername())));
		List<GarmentRequestDTO> res = new ArrayList<GarmentRequestDTO>();
		for(int i = 0; i < garments.size(); ++i) {
			res.add(this.createGarment(garments.get(i), user));
		}
		return res;
	}

	@Transactional
	public GarmentRequestDTO updateGarment(GarmentUpdateRequestDTO garment, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException{
		if(garment.garmentName().length() > this.maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", this.maxGarmentNameLength));
		}

		Garment currentGarment = this.getGarment(garment.garmentId());

		if(!currentGarment.getUser().getUsername().toLowerCase().equals(userDetails.getUsername().toLowerCase())) {
			throw new IllegalArgumentException("User does not have permission to edit this garment");
		}

		currentGarment.setName(garment.garmentName());

		garmentRepository.save(currentGarment);
		return GarmentRequestDTO.toDTO(currentGarment);
	}

	@Transactional
	public void editTags(GarmentTagUpdateRequestDTO garmentUpdate, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		Garment garment = this.getGarment(garmentUpdate.garmentId());

		if(!garment.getUser().getUsername().toLowerCase().equals(userDetails.getUsername().toLowerCase())) {
			throw new IllegalArgumentException("User does not have permission to edit this garment");
		}

		List<Tag> addTags = new ArrayList<Tag>();
		List<Tag> removeTags = new ArrayList<Tag>();

		for(int id : garmentUpdate.addTagIds()) {
			addTags.add(tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id)))));
		}
		for(int id : garmentUpdate.removeTagIds()) {
			removeTags.add(tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id)))));
		}

		if(garment.getTags().size() + garmentUpdate.addTagIds().size() - garmentUpdate.removeTagIds().size() > this.maxTagsPerGarment) {
			throw new IllegalArgumentException(String.format("Too many tags provided when updating a garment, must be at most %d tags", this.maxTagsPerGarment));
		}

		garment.addTag(addTags);
		garment.removeTag(removeTags);
		
		garmentRepository.save(garment);
	}

	@Transactional
	public void editURLs(GarmentURLUpdateRequestDTO garmentUpdate, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		Garment garment = this.getGarment(garmentUpdate.garmentId());

		if(!garment.getUser().getUsername().toLowerCase().equals(userDetails.getUsername().toLowerCase())) {
			throw new IllegalArgumentException("User does not have permission to edit this garment");
		}

		this.validateURLInput(garment, garmentUpdate.addURLs(), garmentUpdate.removeURLs());

		garment.addURL(garmentUpdate.addURLs());
		garment.removeURL(garmentUpdate.removeURLs());

		garmentRepository.save(garment);
	}

	@Transactional
	public void deleteGarment(Integer garmentId, UserDetails userDetails) throws EntityNotFoundException, AccessDeniedException {
		Garment currentGarment = this.getGarment(garmentId);

		//Checking user is the owner of this garment
		if(!userDetails.getUsername().toLowerCase().equals(currentGarment.getUser().getUsername())) {
			throw new AccessDeniedException("User does not have permission to delete this garment");
		}

		garmentRepository.deleteGarmentFromOutfits(garmentId);
		garmentRepository.delete(currentGarment);
	}
}
