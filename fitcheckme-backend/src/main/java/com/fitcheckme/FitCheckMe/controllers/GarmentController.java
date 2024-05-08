package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.services.GarmentService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/garment")
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
		return this.garmentService.getById(id);
	}
	
	@GetMapping("usergarments")
	public List<GarmentRequestDTO> getUserGarments(@RequestParam Integer userId) {
		return this.garmentService.getUserGarments(userId);
	}
	
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createGarment(@Valid @RequestBody GarmentCreateRequestDTO garment, @AuthenticationPrincipal UserDetails userDetails) {
		garmentService.createGarment(garment, userDetails);
	}

	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@Valid @RequestBody GarmentUpdateRequestDTO garment, @AuthenticationPrincipal UserDetails userDetails) {
		this.garmentService.updateGarment(garment, userDetails);
	}
	
	@DeleteMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void deleteGarment(@RequestParam(required = true) Integer id, @AuthenticationPrincipal UserDetails userDetails) {
		this.garmentService.deleteGarment(id, userDetails);
	}
}
