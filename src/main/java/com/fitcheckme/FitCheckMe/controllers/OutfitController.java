package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

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
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.services.OutfitService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/outfit")
@CrossOrigin(origins = "*")
public class OutfitController {
	private final OutfitService outfitService;

	public OutfitController(OutfitService outfitService) {
		this.outfitService = outfitService;
	}

	@GetMapping("all")
	public List<OutfitRequestDTO> getAll() {
		return this.outfitService.getAll();
	}

	@GetMapping("{id}")
	public OutfitRequestDTO getById(@PathVariable Integer id) {
		try {
			return this.outfitService.getById(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("useroutfits")
	public List<OutfitRequestDTO> getUserOutfits(@RequestParam Integer userId) {
		try {
			return this.outfitService.getUserOutfits(userId);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createOutfit(@Valid @RequestBody OutfitCreateRequestDTO outfit) {
		try {
			this.outfitService.createOutfit(outfit);
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@Valid @RequestBody OutfitUpdateRequestDTO outfit) {
		try {
			this.outfitService.updateOutfit(outfit);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeOutfit(@PathVariable Integer id) {
		try {
			this.outfitService.deleteOutfit(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
