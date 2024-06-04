package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateImagesRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.ImageFile;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.OutfitImage;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.ImageFileRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitImageRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

//TODO implement auth permissions for this service
@Service
public class OutfitService {

	@Value("${fitcheckme.max-outfit-name-length}")
	private Integer maxNameLength;

	@Value("${fitcheckme.max-outfit-desc-length}")
	private Integer maxDescLength;

	@Value("${fitcheckme.max-outfit-tags}")
	private Integer maxTagsPerOutfit;

	@Value("${fitcheckme.max-garments-per-outfit}")
	private Integer maxGarmentsPerOutfit;

	private final OutfitRepository outfitRepository;
	private final GarmentRepository garmentRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final OutfitImageRepository outfitImageRepository;
	private final ImageFileRepository imageFileRepository;

	public OutfitService(OutfitRepository outfitRepository, GarmentRepository garmentRepository,
			TagRepository tagRepository, UserRepository userRepository, OutfitImageRepository outfitImageRepository,
			ImageFileRepository imageFileRepository) {
		this.outfitRepository = outfitRepository;
		this.garmentRepository = garmentRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
		this.outfitImageRepository = outfitImageRepository;
		this.imageFileRepository = imageFileRepository;
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OUTFIT_ADMIN')")
	public List<OutfitRequestDTO> getAll() {
		return outfitRepository.findAllByOrderByIdAsc().stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

	@Transactional
	private Outfit getOutfit(Integer id) throws EntityNotFoundException {
		return outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id))));
	}

	public OutfitRequestDTO getById(Integer id) throws EntityNotFoundException {
		return OutfitRequestDTO.toDTO(outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id)))));
	}

	public List<OutfitRequestDTO> getById(List<Integer> ids) throws EntityNotFoundException {
		if(ids.isEmpty()) {
			return new ArrayList<OutfitRequestDTO>();
		}
		
		List<Outfit> res = outfitRepository.findAllById(ids);
		
		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			List<Integer> foundIds = res.stream().map(outfit -> outfit.getId()).toList();
			throw new EntityNotFoundException(String.format("Outfits not found with IDs: %s", ids.stream().filter(id -> !foundIds.contains(id)).map(id -> String.valueOf(id)).toList()));
		}

		return res.stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

	public boolean exists(Integer id) {
		return outfitRepository.existsById(id);
	}

	public List<OutfitRequestDTO> getUserOutfits(Integer userId) throws EntityNotFoundException {
		// Checking the user exists
		if(!userRepository.existsById(userId)) {
			throw new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(userId)));
		}
		return this.outfitRepository.findByUserId(userId).stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

	public List<OutfitRequestDTO> getUserOutfits(String username) throws EntityNotFoundException {
		// Checking the user exists
		if(!userRepository.existsByUsernameIgnoreCase(username)) {
			throw new EntityNotFoundException(String.format("User not found with username: %s", username));
		}
		return this.outfitRepository.findByUser_UsernameIgnoreCase(username).stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

	@Transactional
	public OutfitRequestDTO createOutfit(OutfitCreateRequestDTO outfit, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(outfit.outfitName().length() > this.maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", this.maxNameLength));
		}
		if(outfit.outfitDesc().length() > this.maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", this.maxDescLength));
		}

		Set<Tag> tags;
		if(outfit.outfitTags() != null) {
			tags = new HashSet<>(tagRepository.findAllById(outfit.outfitTags()));
			if (tags.size() != outfit.outfitTags().size()) {
				List<Integer> missingTags = outfit.outfitTags().stream().filter(tagId -> tags.stream().noneMatch(tag -> tag.getId().equals(tagId))).toList();
				throw new EntityNotFoundException(String.format("Tags not found with IDs: %s", missingTags));
			}
			if (tags.size() > this.maxTagsPerOutfit) {
				throw new IllegalArgumentException(String.format("Too many tags provided when creating outfit, must be at most %d tags", this.maxTagsPerOutfit));
			}
		}
		else {
			tags = Set.of();
		}
		
		Set<Garment> garments;
		if(outfit.garments() != null) {
			garments = new HashSet<>(garmentRepository.findAllById(outfit.garments()));
			if (garments.size() != outfit.garments().size()) {
				List<Integer> missingGarments = outfit.garments().stream().filter(garmentId -> garments.stream().noneMatch(garment -> garment.getId().equals(garmentId))).toList();
				throw new EntityNotFoundException(String.format("Garments not found with IDs: %s", missingGarments));
			}
			if (garments.size() > this.maxGarmentsPerOutfit) {
				throw new IllegalArgumentException(String.format("Too many garments provided when creating outfit, must be at most %d garments", this.maxGarmentsPerOutfit));
			}
		}
		else {
			garments = Set.of();
		}

		User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %d", userDetails.getUserId())));

		Outfit newOutfit = new Outfit(user, outfit.outfitName(), outfit.outfitDesc(), LocalDateTime.now(), garments, tags);
		return OutfitRequestDTO.toDTO(this.outfitRepository.save(newOutfit));
	}

	@Transactional
	public OutfitRequestDTO updateOutfit(OutfitUpdateRequestDTO outfit, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException, AccessDeniedException {
		if(outfit.outfitName() != null && outfit.outfitName().length() > this.maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", this.maxNameLength));
		}
		if(outfit.outfitDesc() != null && outfit.outfitDesc().length() > this.maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", this.maxDescLength));
		}

		Outfit currentOutfit = this.getOutfit(outfit.outfitId());

		if(currentOutfit.getUser().getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to edit this outfit");
		}

		if(outfit.outfitName() != null) {
			currentOutfit.setName(outfit.outfitName());
		}
		if(outfit.outfitDesc() != null) {
			currentOutfit.setDesc(outfit.outfitDesc());
		}
		if(outfit.addGarmentIds() != null || outfit.removeGarmentIds() != null) {
			this.editGarments(currentOutfit, outfit.addGarmentIds(), outfit.removeGarmentIds(), userDetails);
		}
		if(outfit.addTagIds() != null || outfit.removeTagIds() != null) {
			this.editTags(currentOutfit, outfit.addTagIds(), outfit.removeTagIds(), userDetails);
		}
		
		if (outfit.outfitName() != null ||
			outfit.outfitDesc() != null ||
			outfit.addGarmentIds() != null ||
			outfit.removeGarmentIds() != null ||
			outfit.addTagIds() != null ||
			outfit.removeTagIds() != null) {
			return OutfitRequestDTO.toDTO(this.outfitRepository.save(currentOutfit));
		}
		else {
			return OutfitRequestDTO.toDTO(currentOutfit);
		}
		
	}

	@Transactional
	private void editGarments(Outfit currentOutfit, Set<Integer> addGarmentIds, Set<Integer> removeGarmentIds, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(currentOutfit.getUser().getId() != userDetails.getUserId()) {
			throw new IllegalArgumentException("User does not have permissions to edit this outfit");
		}

		Set<Garment> addGarments = addGarmentIds != null && !addGarmentIds.isEmpty() ? new HashSet<>(garmentRepository.findAllById(addGarmentIds)) : new HashSet<Garment>();
		Set<Garment> removeGarments = removeGarmentIds != null && !removeGarmentIds.isEmpty() ? new HashSet<>(garmentRepository.findAllByOutfitIdAndId(removeGarmentIds, currentOutfit.getId())) : new HashSet<Garment>();

		if(addGarmentIds != null && addGarments.size() != addGarmentIds.size()) {
			throw new EntityNotFoundException("One or more garments not found in add list");
		}
		if(removeGarmentIds != null && removeGarments.size() != removeGarmentIds.size()) {
			throw new EntityNotFoundException("One or more garments not found in remove list");
		}

		for(Garment garment : addGarments) {
			if(currentOutfit.getGarments().contains(garment)) {
				throw new IllegalArgumentException("One or more garments already in outfit");
			}
			if(removeGarments.contains(garment)) {
				throw new IllegalArgumentException("One or more garments in both add and remove lists");
			}
		}

		if(currentOutfit.getGarments().size() + addGarments.size() - removeGarments.size() > this.maxGarmentsPerOutfit) {
			throw new IllegalArgumentException(String.format("Too many garments provided when updating outfit, must be at most %s garments", this.maxGarmentsPerOutfit));
		}

		currentOutfit.addGarment(addGarments);
		currentOutfit.removeGarment(removeGarments);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	private void editTags(Outfit currentOutfit, Set<Integer> addTagIds, Set<Integer> removeTagIds, CustomUserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(currentOutfit.getUser().getId() != userDetails.getUserId()) {
			throw new IllegalArgumentException("User does not have permissions to edit this outfit");
		}

		List<Tag> addTags = addTagIds != null && !addTagIds.isEmpty() ? tagRepository.findAllById(addTagIds) : new ArrayList<Tag>();
		List<Tag> removeTags = removeTagIds != null && !removeTagIds.isEmpty() ? tagRepository.findAllById(removeTagIds) : new ArrayList<Tag>();

		if(addTagIds != null && addTags.size() != addTagIds.size()) {
			throw new EntityNotFoundException("One or more tags not found in add list");
		}
		if(removeTagIds != null && removeTags.size() != removeTagIds.size()) {
			throw new EntityNotFoundException("One or more tags not found in remove list");
		}

		for(Tag tag : addTags) {
			if(currentOutfit.getTags().contains(tag)) {
				throw new IllegalArgumentException("One or more tags already in outfit");
			}
			if(removeTags.contains(tag)) {
				throw new IllegalArgumentException("One or more tags in both add and remove lists");
			}
		}

		if (currentOutfit.getTags().size() + addTags.size() - removeTags.size() > this.maxTagsPerOutfit) {
			throw new IllegalArgumentException(String.format("Too many tags provided when updating outfit, must be at most %d tags", this.maxTagsPerOutfit));
		}

		currentOutfit.addTag(addTags);
		currentOutfit.removeTag(removeTags);

		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public OutfitRequestDTO updateOutfitImages(OutfitUpdateImagesRequestDTO updateDTO, CustomUserDetails userDetails) {
		Outfit currentOutfit = this.outfitRepository.findById(updateDTO.outfitId()).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(updateDTO.outfitId()))));

		if(currentOutfit.getUser().getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to edit this outfit");
		}

		if(updateDTO.addImageIds() != null && !updateDTO.addImageIds().isEmpty()) {
			Set<Integer> existingImageIds = currentOutfit.getImages().stream().map(image -> image.getImage().getId()).collect(Collectors.toSet());
			for(Integer imageId : updateDTO.addImageIds()) {
				ImageFile image = imageFileRepository.findById(imageId).orElseThrow(() -> new EntityNotFoundException(String.format("Image not found with ID: %s", String.valueOf(imageId))));
				if(image.getUser().getId() != userDetails.getUserId()) {
					throw new AccessDeniedException("User does not have permission to add this image to the outfit");
				}
				if(existingImageIds.contains(imageId)) {
					throw new IllegalArgumentException(String.format("Image already in outfit with path: %s", image.getImagePath()));
				}
				OutfitImage newImage = outfitImageRepository.save(new OutfitImage(image, currentOutfit));
				currentOutfit.addImage(newImage);
			}
		}
		if(updateDTO.removeImageIds() != null && !updateDTO.removeImageIds().isEmpty()) {
			for(Integer imageId : updateDTO.removeImageIds()) {
				outfitImageRepository.deleteByOutfit_OutfitIdAndImage_ImageFileId(currentOutfit.getId(), imageId);
			}
		}

		return OutfitRequestDTO.toDTO(outfitRepository.save(currentOutfit));
	}

	@Transactional
	public void deleteOutfit(Integer id, CustomUserDetails userDetails) throws AccessDeniedException, EntityNotFoundException {
		Outfit currentOutfit = this.getOutfit(id);

		if(currentOutfit.getUser().getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to delete this outfit");
		}

		this.outfitRepository.deleteOutfitFromGarments(id);
		this.outfitRepository.deleteById(id);
	}
}
