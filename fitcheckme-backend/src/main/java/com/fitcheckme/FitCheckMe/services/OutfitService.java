package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
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

	public OutfitService(OutfitRepository outfitRepository, GarmentRepository garmentRepository, TagRepository tagRepository, UserRepository userRepository) {
		this.outfitRepository = outfitRepository;
		this.garmentRepository = garmentRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
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
			throw new EntityNotFoundException(String.format("%d/%d outfits in list not found", ids.size() - res.size(), ids.size()));
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
			throw new EntityNotFoundException(String.format("User not found with ID: %s", username));
		}
		return this.outfitRepository.findByUser_UsernameIgnoreCase(username).stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

	@Transactional
	public OutfitRequestDTO createOutfit(OutfitCreateRequestDTO outfit, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(outfit.outfitName().length() > this.maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", this.maxNameLength));
		}
		if(outfit.outfitDesc().length() > this.maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", this.maxDescLength));
		}

		List<Garment> garments = garmentRepository.findAllById(outfit.garments());
		if(garments.size() != outfit.garments().size()) {
			throw new IllegalArgumentException("One or more garments not found");
		}

		List<Tag> tags = tagRepository.findAllById(outfit.outfitTags());
		if(tags.size() != outfit.outfitTags().size()) {
			throw new IllegalArgumentException("One or more tags not found");
		}

		User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException(String.format("User '%s' not found")));

		Outfit newOutfit = new Outfit(user, outfit.outfitName(), outfit.outfitDesc(), LocalDateTime.now(), garments, tags);
		this.outfitRepository.save(newOutfit);
		return OutfitRequestDTO.toDTO(newOutfit);
	}

	public OutfitRequestDTO updateOutfit(OutfitUpdateRequestDTO outfit, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(outfit.outfitName() != null && outfit.outfitName().length() > this.maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", this.maxNameLength));
		}
		if(outfit.outfitDesc() != null && outfit.outfitDesc().length() > this.maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", this.maxDescLength));
		}

		Outfit currentOutfit = this.getOutfit(outfit.outfitId());

		if(!currentOutfit.getUser().getUsername().equals(userDetails.getUsername())) {
			throw new IllegalArgumentException("User does not have permissions to edit this outfit");
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
		
		this.outfitRepository.save(currentOutfit);
		return OutfitRequestDTO.toDTO(currentOutfit);
	}

	@Transactional
	private void editGarments(Outfit currentOutfit, List<Integer> addGarmentIds, List<Integer> removeGarmentIds, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(!currentOutfit.getUser().getUsername().equals(userDetails.getUsername())) {
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
			throw new IllegalArgumentException(String.format("Outfit can only have up to %s garments", this.maxGarmentsPerOutfit));
		}

		currentOutfit.addGarment(addGarments);
		currentOutfit.removeGarment(removeGarments);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	private void editTags(Outfit currentOutfit, List<Integer> addTagIds, List<Integer> removeTagIds, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(!currentOutfit.getUser().getUsername().equals(userDetails.getUsername())) {
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
			throw new IllegalArgumentException(String.format("Outfit can only have up to %d tags", this.maxTagsPerOutfit));
		}

		currentOutfit.addTag(addTags);
		currentOutfit.removeTag(removeTags);

		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void deleteOutfit(Integer id, UserDetails userDetails) throws AccessDeniedException {
		Outfit currentOutfit = this.getOutfit(id);

		if(!currentOutfit.getUser().getUsername().toLowerCase().equals(userDetails.getUsername().toLowerCase())) {
			throw new AccessDeniedException("User does not have permissions to delete this outfit");
		}

		this.outfitRepository.deleteOutfitFromGarments(id);
		this.outfitRepository.deleteById(id);
	}
}
