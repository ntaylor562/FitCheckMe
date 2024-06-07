package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateImagesRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.GarmentImage;
import com.fitcheckme.FitCheckMe.models.ImageFile;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentImageRepository;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.ImageFileRepository;
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
	private final GarmentImageRepository garmentImageRepository;
	private final ImageFileRepository imageFileRepository;

	public GarmentService(GarmentRepository garmentRepository, TagRepository tagRepository, UserRepository userRepository, GarmentImageRepository garmentImageRepository, ImageFileRepository imageFileRepository) {
		this.garmentRepository = garmentRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
		this.garmentImageRepository = garmentImageRepository;
		this.imageFileRepository = imageFileRepository;
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
		return this.garmentRepository.findByUserIdOrderByIdAsc(userId).stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}

	public List<GarmentRequestDTO> getUserGarments(String username) throws EntityNotFoundException {
		//Checking the user exists
		if(!this.userRepository.existsByUsernameIgnoreCase(username)) {
			throw new EntityNotFoundException(String.format("User not found with username: %s", username));
		}
		return this.garmentRepository.findByUser_UsernameIgnoreCaseOrderByIdAsc(username).stream().map(garment -> GarmentRequestDTO.toDTO(garment)).toList();
	}

	@Transactional
	private GarmentRequestDTO createGarment(GarmentCreateRequestDTO garment, User user) throws EntityNotFoundException, IllegalArgumentException {
		String garmentName = garment.garmentName() != null ? garment.garmentName().strip() : "";
		Set<String> garmentURLs = new HashSet<>(garment.urls().stream().map(url -> url.strip()).toList());
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
			tags.addAll(foundTags);
		}
		
		//TODO think about performing security checks on URLs
		Garment newGarment = new Garment(user, garment.garmentName(), garmentURLs, tags);
		return GarmentRequestDTO.toDTO(garmentRepository.save(newGarment));
	}

	@Transactional
	public GarmentRequestDTO createGarment(GarmentCreateRequestDTO garment, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %d", userDetails.getUserId())));
		return this.createGarment(garment, user);
	}

	@Transactional
	public List<GarmentRequestDTO> createGarment(List<GarmentCreateRequestDTO> garments, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException{
		User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %d", userDetails.getUserId())));
		List<GarmentRequestDTO> res = new ArrayList<GarmentRequestDTO>();
		for(int i = 0; i < garments.size(); ++i) {
			res.add(this.createGarment(garments.get(i), user));
		}
		return res;
	}

	@Transactional
	public GarmentRequestDTO updateGarment(GarmentUpdateRequestDTO garment, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(garment.garmentName() != null && garment.garmentName().length() > this.maxGarmentNameLength) {
			throw new IllegalArgumentException(String.format("Garment name too long, must be at most %d characters", this.maxGarmentNameLength));
		}

		Garment currentGarment = this.getGarment(garment.garmentId());

		if(currentGarment.getUser().getId() != userDetails.getUserId()) {
			throw new IllegalArgumentException("User does not have permission to edit this garment");
		}

		if(garment.garmentName() != null && !garment.garmentName().equals(currentGarment.getName())) {
			currentGarment.setName(garment.garmentName());
		}
		if((garment.addURLs() != null && !garment.addURLs().isEmpty()) || (garment.removeURLs() != null && !garment.removeURLs().isEmpty())) {
			this.editURLs(currentGarment, garment.addURLs(), garment.removeURLs(), userDetails);
		}
		if((garment.addTagIds() != null && !garment.addTagIds().isEmpty()) || (garment.removeTagIds() != null && !garment.removeTagIds().isEmpty())) {
			this.editTags(currentGarment, garment.addTagIds(), garment.removeTagIds(), userDetails);
		}

		if ((garment.garmentName() != null && !garment.garmentName().equals(currentGarment.getName()))
				|| (garment.addURLs() != null && !garment.addURLs().isEmpty())
				|| (garment.removeURLs() != null && !garment.removeURLs().isEmpty())
				|| (garment.addTagIds() != null && !garment.addTagIds().isEmpty())
				|| (garment.removeTagIds() != null && !garment.removeTagIds().isEmpty())) {
			return GarmentRequestDTO.toDTO(garmentRepository.save(currentGarment));
		} else {
			return GarmentRequestDTO.toDTO(currentGarment);
		}
	}

	@Transactional
	private void editTags(Garment currentGarment, Set<Integer> addTagIds, Set<Integer> removeTagIds, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(currentGarment.getUser().getId() != userDetails.getUserId()) {
			throw new IllegalArgumentException("User does not have permission to edit this garment");
		}

		List<Tag> addTags = addTagIds != null && !addTagIds.isEmpty()
				? tagRepository.findAllById(addTagIds)
				: new ArrayList<Tag>();
		List<Tag> removeTags = removeTagIds != null && !removeTagIds.isEmpty()
				? tagRepository.findAllByGarmentIdAndIdsIn(currentGarment.getId(), removeTagIds)
				: new ArrayList<Tag>();

		if (addTagIds != null && addTags.size() != addTagIds.size()) {
			throw new EntityNotFoundException("One or more tags not found in add list");
		}
		if (removeTagIds != null && removeTags.size() != removeTagIds.size()) {
			throw new EntityNotFoundException("One or more tags not found in remove list");
		}

		for (Tag tag : addTags) {
			if (currentGarment.getTags().contains(tag)) {
				throw new IllegalArgumentException("One or more tags already in garment");
			}
		}

		if(currentGarment.getTags().size() + addTags.size() - removeTags.size() > this.maxTagsPerGarment) {
			throw new IllegalArgumentException(String.format("Too many tags provided when updating a garment, must be at most %d tags", this.maxTagsPerGarment));
		}

		currentGarment.addTag(addTags);
		currentGarment.removeTag(removeTags);
		
		this.garmentRepository.save(currentGarment);
	}

	@Transactional
	public GarmentRequestDTO updateGarmentImages(GarmentUpdateImagesRequestDTO updateDTO,
			CustomUserDetails userDetails) {
		Garment currentGarment = this.garmentRepository.findById(updateDTO.garmentId())
				.orElseThrow(() -> new EntityNotFoundException(
						String.format("Garment not found with ID: %s", String.valueOf(updateDTO.garmentId()))));

		if (currentGarment.getUser().getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to edit this garment");
		}

		if (updateDTO.addImageIds() != null || updateDTO.removeImageIds() != null) {
			Set<Integer> existingImageIds = currentGarment.getImages().stream().map(image -> image.getImage().getId())
					.collect(Collectors.toSet());
			if (updateDTO.addImageIds() != null && !updateDTO.addImageIds().isEmpty()) {
				for (Integer imageId : updateDTO.addImageIds()) {
					ImageFile image = imageFileRepository.findById(imageId)
							.orElseThrow(() -> new EntityNotFoundException(
									String.format("Image not found with ID: %s", String.valueOf(imageId))));
					if (image.getUser().getId() != userDetails.getUserId()) {
						throw new AccessDeniedException(
								"User does not have permission to add this image to the garment");
					}
					if (existingImageIds.contains(imageId)) {
						throw new IllegalArgumentException(
								String.format("Image already in garment with ID: %d", image.getId()));
					}
					GarmentImage newImage = garmentImageRepository.save(new GarmentImage(image, currentGarment));
					currentGarment.addImage(newImage);
				}
			}
			if (updateDTO.removeImageIds() != null && !updateDTO.removeImageIds().isEmpty()) {
				for (Integer imageId : updateDTO.removeImageIds()) {
					garmentImageRepository.deleteByGarment_GarmentIdAndImage_ImageFileId(currentGarment.getId(),
							imageId);
					if (!currentGarment.removeImage(imageId)) {
						throw new EntityNotFoundException(
								String.format("Image not found in garment with ID: %s", String.valueOf(imageId)));
					}
				}
			}
		}

		if (updateDTO.addImageIds() != null && updateDTO.removeImageIds() != null) {
			Set<Integer> intersection = new HashSet<>(updateDTO.addImageIds());
			intersection.retainAll(updateDTO.removeImageIds());
			if (!intersection.isEmpty()) {
				throw new IllegalArgumentException("Image IDs cannot be in both add and remove lists");
			}
		}

		return GarmentRequestDTO.toDTO(garmentRepository.save(currentGarment));
	}

	@Transactional
	private void editURLs(Garment currentGarment, Set<String> addURLsInput, Set<String> removeURLsInput, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(currentGarment.getUser().getId() != userDetails.getUserId()) {
			throw new IllegalArgumentException("User does not have permission to edit this garment");
		}

		Set<String> addURLs = addURLsInput != null ? addURLsInput.stream().map(url -> url.strip()).collect(Collectors.toSet()) : new HashSet<String>();
		Set<String> removeURLs = removeURLsInput != null ? removeURLsInput.stream().map(url -> url.strip()).collect(Collectors.toSet()) : new HashSet<String>();
		
		if(!currentGarment.getURLs().containsAll(removeURLs)) {
			throw new IllegalArgumentException("One or more URLs not in garment");
		}

		for (String url : addURLs) {
			if (url.length() > maxGarmentURLLength) {
				throw new IllegalArgumentException(String.format(
						"Garment URL %s too long, must be at most %d characters", url, this.maxGarmentURLLength));
			}

			if(currentGarment.getURLs().contains(url)) {
				throw new IllegalArgumentException("One or more URLs already in garment");
			}
		}

		if (addURLs.size() + currentGarment.getURLs().size() - removeURLs.size() > this.maxURLsPerGarment) {
			throw new IllegalArgumentException(String.format(
					"Too many URLs provided when creating a garment, must be at most %d URLs", this.maxURLsPerGarment));
		}

		currentGarment.addURL(addURLs);
		currentGarment.removeURL(removeURLs);

		garmentRepository.save(currentGarment);
	}

	@Transactional
	public void deleteGarment(Integer garmentId, CustomUserDetails userDetails) throws EntityNotFoundException, AccessDeniedException {
		Garment currentGarment = this.getGarment(garmentId);

		//Checking user is the owner of this garment
		if(currentGarment.getUser().getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to delete this garment");
		}

		garmentRepository.deleteGarmentFromOutfits(garmentId);
		garmentRepository.delete(currentGarment);
	}
}
