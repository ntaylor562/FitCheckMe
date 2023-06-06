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
import com.fitcheckme.FitCheckMe.repositories.OutfitControllerRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/outfit")
@CrossOrigin(origins = "*")
public class OutfitController {
	private final OutfitControllerRepository repository;

	@Autowired
	public OutfitController(OutfitControllerRepository repo) {
		this.repository = repo;
	}

	//Retrieve all outfits
	@GetMapping("")
	public List<Outfit> findAll() {
		return this.repository.findAll();
	}

	//Retrieve an outfit by ID
	@GetMapping("{id}")
	public Outfit findById(@PathVariable Integer id) {
		return this.repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not get"));
	}

	//Create an outfit
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createOutfit(@Valid @RequestBody Outfit outfit) {
		this.repository.createOutfit(outfit);
	}

	//Updating an outfit
	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@RequestBody Outfit outfit) {
		Optional<Outfit> repoOutfit = this.repository.findById(outfit.id());
		if(!repoOutfit.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not update");
		}
		
		this.repository.updateOutfit(outfit);
	}

	//Remove an outfit
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeOutfit(@PathVariable Integer id) {
		Optional<Outfit> outfit = this.repository.findById(id);
		if(!outfit.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of outfit not found, could not update");
		}
		
		this.repository.removeOutfit(id);
	}
}
