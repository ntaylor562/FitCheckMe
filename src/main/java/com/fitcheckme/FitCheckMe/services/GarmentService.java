package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;

import jakarta.persistence.EntityNotFoundException;
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
	private TagService tagService;

	public List<Garment> getAll() {
		return garmentRepository.findAll();
	}

	public Garment getById(Long id) {
		return garmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Garment not found with ID: %s", String.valueOf(id))));
	}

	public List<Garment> getById(List<Long> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<Garment>();
		}

		List<Garment> res = garmentRepository.findAllById(ids);

		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			throw new EntityNotFoundException(String.format("%d/%d garments in list not found", ids.size() - res.size(), ids.size()));
		}

		return garmentRepository.findAllById(ids);
	}

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

		//TODO think about performing security checks on URLs
		Garment newGarment = new Garment(garment.garmentName(), garment.garmentURLs(), tagService.getById(garment.garmentTagIds()));
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

	//TODO implement
	public void updateGarment(/* make an GarmentUpdateRequestDTO? */) {
		//TODO Think about making an add/remove tag method
	}

	//TODO implement
	public void deleteGarment() {

	}
}
