package com.fitcheckme.FitCheckMe.services.get_services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GarmentGetService {
	@Autowired
	private GarmentRepository garmentRepository;

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
			throw new EntityNotFoundException(String.format("%d/%d garments in list not found", ids.size() - res.size(), ids.size()));
		}

		return garmentRepository.findAllById(ids);
	}
}
