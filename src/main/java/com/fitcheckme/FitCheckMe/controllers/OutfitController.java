package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.services.OutfitService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/outfit")
@CrossOrigin(origins = "*")
public class OutfitController {
	@Autowired
	private OutfitService outfitService;

	//Retrieve all outfits
	@GetMapping("")
	public List<Outfit> findAll() {
		return this.outfitService.getAll();
	}

	//Retrieve an outfit by ID
	@GetMapping("{id}")
	public Outfit findById(@PathVariable Long id) {
		try {
			return this.outfitService.getById(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not get");
		}
	}

	//Create an outfit
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createOutfit(@Valid @RequestBody OutfitCreateRequestDTO outfit) {
		this.outfitService.createOutfit(outfit);
	}

	//Updating an outfit
	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@RequestBody OutfitUpdateRequestDTO outfit) {
		try {
			this.outfitService.updateOutfit(outfit);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not update");
		}
	}

	//Remove an outfit
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeOutfit(@PathVariable Long id) {
		try {
			this.outfitService.deleteOutfit(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not delete");
		}
	}
}
