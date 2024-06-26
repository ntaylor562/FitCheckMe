package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateImagesRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.services.GarmentService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	public ResponseEntity<List<GarmentRequestDTO>> getAll() {
		return new ResponseEntity<List<GarmentRequestDTO>>(this.garmentService.getAll(), HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<GarmentRequestDTO> getById(@RequestParam Integer id) {
		return new ResponseEntity<GarmentRequestDTO>(this.garmentService.getById(id), HttpStatus.OK);
	}
	
	@GetMapping("usergarments")
	public ResponseEntity<List<GarmentRequestDTO>> getUserGarments(@RequestParam(required = false) Integer userId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		if(userId == null) {
			return new ResponseEntity<List<GarmentRequestDTO>>(this.garmentService.getUserGarments(userDetails.getUserId()), HttpStatus.OK);
		}
		return new ResponseEntity<List<GarmentRequestDTO>>(this.garmentService.getUserGarments(userId), HttpStatus.OK);
	}
	
	@PostMapping("create")
	public ResponseEntity<GarmentRequestDTO> createGarment(@Valid @RequestBody GarmentCreateRequestDTO garment, @AuthenticationPrincipal CustomUserDetails userDetails) {
		return new ResponseEntity<GarmentRequestDTO>(garmentService.createGarment(garment, userDetails), HttpStatus.CREATED);
	}

	@PutMapping("edit")
	public ResponseEntity<GarmentRequestDTO> updateGarment(@Valid @RequestBody GarmentUpdateRequestDTO garment, @AuthenticationPrincipal CustomUserDetails userDetails) {
		return new ResponseEntity<GarmentRequestDTO>(this.garmentService.updateGarment(garment, userDetails), HttpStatus.OK);
	}

	@PutMapping("editimages")
	public ResponseEntity<GarmentRequestDTO> updateImages(@Valid @RequestBody GarmentUpdateImagesRequestDTO garmentImages, @AuthenticationPrincipal CustomUserDetails userDetails) {
		return new ResponseEntity<GarmentRequestDTO>(this.garmentService.updateGarmentImages(garmentImages, userDetails), HttpStatus.OK);
	}
	
	@DeleteMapping("")
	@ResponseStatus(HttpStatus.OK)
	public void deleteGarment(@RequestParam(required = true) Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
		this.garmentService.deleteGarment(id, userDetails);
	}
}
