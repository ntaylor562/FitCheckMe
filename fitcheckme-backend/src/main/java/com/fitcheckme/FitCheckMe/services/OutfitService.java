package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitGarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitTagUpdateRequestDTO;
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
	@Value("${fitcheckme.max-outfit-desc-length}")
	private int maxDescLength;

	@Value("${fitcheckme.max-outfit-name-length}")
	private int maxNameLength;
	
	private final OutfitRepository outfitRepository;
	private final GarmentRepository garmentRepository;
	private final TagRepository tagRepository;
	private final UserService userService;
	private final UserRepository userRepository;

	public OutfitService(OutfitRepository outfitRepository, GarmentRepository garmentRepository, TagRepository tagRepository, UserService userService, UserRepository userRepository) {
		this.outfitRepository = outfitRepository;
		this.garmentRepository = garmentRepository;
		this.tagRepository = tagRepository;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	public List<OutfitRequestDTO> getAll() {
		return outfitRepository.findAllByOrderByIdAsc().stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

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
		if(!userService.exists(userId)) {
			throw new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(userId)));
		}
		return this.outfitRepository.findByUserId(userId).stream().map(outfit -> OutfitRequestDTO.toDTO(outfit)).toList();
	}

	//TODO add auth
	@Transactional
	public OutfitRequestDTO createOutfit(OutfitCreateRequestDTO outfit, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		if(outfit.outfitName().length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		if(outfit.outfitDesc().length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
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
		if(outfit.outfitName() != null && outfit.outfitName().length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		if(outfit.outfitDesc() != null && outfit.outfitDesc().length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
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
		
		this.outfitRepository.save(currentOutfit);
		return OutfitRequestDTO.toDTO(currentOutfit);
	}

	//TODO add auth so only the owner can do this
	@Transactional
	public void editGarments(OutfitGarmentUpdateRequestDTO outfitUpdate, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		Outfit currentOutfit = this.getOutfit(outfitUpdate.outfitId());

		if(!currentOutfit.getUser().getUsername().equals(userDetails.getUsername())) {
			throw new IllegalArgumentException("User does not have permissions to edit this outfit");
		}

		Set<Garment> addGarments = outfitUpdate.addGarmentIds() != null && !outfitUpdate.addGarmentIds().isEmpty() ? new HashSet<>(garmentRepository.findAllById(outfitUpdate.addGarmentIds())) : new HashSet<Garment>();
		Set<Garment> removeGarments = outfitUpdate.removeGarmentIds() != null && !outfitUpdate.removeGarmentIds().isEmpty() ? new HashSet<>(garmentRepository.findAllByOutfitIdAndId(outfitUpdate.removeGarmentIds(), currentOutfit.getId())) : new HashSet<Garment>();

		if(outfitUpdate.addGarmentIds() != null && addGarments.size() != outfitUpdate.addGarmentIds().size()) {
			throw new EntityNotFoundException("One or more garments not found in add list");
		}
		
		if(outfitUpdate.removeGarmentIds() != null && removeGarments.size() != outfitUpdate.removeGarmentIds().size()) {
			throw new EntityNotFoundException("One or more garments not found in remove list");
		}

		if(addGarments.isEmpty() && removeGarments.isEmpty()) {
			throw new IllegalArgumentException("No garments to add or remove");
		}

		for(Garment garment : addGarments) {
			if(currentOutfit.getGarments().contains(garment)) {
				throw new IllegalArgumentException("One or more garments already in outfit");
			}
			if(removeGarments.contains(garment)) {
				throw new IllegalArgumentException("One or more garments in both add and remove lists");
			}
		}

		currentOutfit.addGarment(addGarments);
		currentOutfit.removeGarment(removeGarments);
		this.outfitRepository.save(currentOutfit);
	}

	@Transactional
	public void editTags(OutfitTagUpdateRequestDTO outfitUpdate, UserDetails userDetails) throws EntityNotFoundException, IllegalArgumentException {
		Outfit currentOutfit = this.getOutfit(outfitUpdate.outfitId());

		if(!currentOutfit.getUser().getUsername().equals(userDetails.getUsername())) {
			throw new IllegalArgumentException("User does not have permissions to edit this outfit");
		}

		List<Tag> addTags = tagRepository.findAllById(outfitUpdate.addTagIds());
		List<Tag> removeTags = tagRepository.findAllById(outfitUpdate.removeTagIds());

		if (addTags.size() + removeTags.size() != outfitUpdate.addTagIds().size() + outfitUpdate.removeTagIds().size()) {
			throw new EntityNotFoundException("One or more tags not found");
		}

		currentOutfit.addTag(addTags);
		currentOutfit.removeTag(removeTags);

		this.outfitRepository.save(currentOutfit);
	}

	//TODO implement (must update all dependent tables)
	public void deleteOutfit(Integer id, UserDetails userDetails) {
		
	}
}
