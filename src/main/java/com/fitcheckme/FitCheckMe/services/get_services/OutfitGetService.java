package com.fitcheckme.FitCheckMe.services.get_services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OutfitGetService {
	@Autowired
	private OutfitRepository outfitRepository;

	public List<Outfit> getAll() {
		return outfitRepository.findAll();
	}

	public Outfit getById(Integer id) {
		return outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Outfit not found with ID: %s", String.valueOf(id))));
	}

	public List<Outfit> getById(List<Integer> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<Outfit>();
		}
		
		List<Outfit> res = outfitRepository.findAllById(ids);
		
		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			throw new EntityNotFoundException(String.format("%d/%d outfits in list not found", ids.size() - res.size(), ids.size()));
		}

		return res;
	}
}
