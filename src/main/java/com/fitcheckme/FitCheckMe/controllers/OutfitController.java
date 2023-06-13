package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;
import java.util.Optional;

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

import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.services.OutfitService;

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
		return this.outfitService.getOutfitById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not get"));
	}

	//Create an outfit
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createOutfit(@Valid @RequestBody Outfit outfit) {
		this.outfitService.createOutfit(outfit);
	}

	//Updating an outfit
	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@RequestBody Outfit outfit) {
		//TODO Move this logic to the service and return the response exceptions from here after catching exceptions from the service
		Optional<Outfit> repoOutfit = this.outfitService.getOutfitById(outfit.getId());
		if(!repoOutfit.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not update");
		}
		
		this.outfitService.updateOutfit(outfit);
	}

	//Remove an outfit
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeOutfit(@PathVariable Long id) {
		//TODO Move this logic to the service and return the response exceptions from here after catching exceptions from the service
		Optional<Outfit> outfit = this.outfitService.getOutfitById(id);
		if(!outfit.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not update");
		}
		
		this.outfitService.deleteOutfit(id);
	}
}
