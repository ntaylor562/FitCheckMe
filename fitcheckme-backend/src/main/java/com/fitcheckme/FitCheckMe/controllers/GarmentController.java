package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.services.GarmentService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/garment")
@CrossOrigin(origins = "http://localhost:3000")
public class GarmentController {
	private final GarmentService garmentService;

	public GarmentController(GarmentService garmentService) {
		this.garmentService = garmentService;
	}

	@GetMapping("all")
	public List<GarmentRequestDTO> getAll() {
		return this.garmentService.getAll();
	}

	@GetMapping("")
	public GarmentRequestDTO getById(@RequestParam Integer id) {
		try {
			return this.garmentService.getById(id);
		} 
		catch (EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("usergarments")
	public List<GarmentRequestDTO> getUserGarments(@RequestParam Integer userId) {
		try {
			return this.garmentService.getUserGarments(userId);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createGarment(@Valid @RequestBody GarmentCreateRequestDTO garment, @AuthenticationPrincipal UserDetails userDetails) {
		try {
			garmentService.createGarment(garment, userDetails.getUsername());
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@Valid @RequestBody GarmentUpdateRequestDTO garment) {
		try {
			this.garmentService.updateGarment(garment);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@DeleteMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteGarment(@RequestParam Integer id) {
		try {
			this.garmentService.deleteGarment(id);
		}
		catch (EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
