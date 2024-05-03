package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitGarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.services.OutfitService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/outfit")
@CrossOrigin(origins = "http://localhost:3000")
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
		return this.outfitService.getById(id);
	}

	@GetMapping("useroutfits")
	public List<OutfitRequestDTO> getUserOutfits(@RequestParam Integer userId) {
		return this.outfitService.getUserOutfits(userId);
	}
	

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createOutfit(@Valid @RequestBody OutfitCreateRequestDTO outfit, @AuthenticationPrincipal UserDetails userDetails) {
		this.outfitService.createOutfit(outfit, userDetails);
	}

	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfit(@Valid @RequestBody OutfitUpdateRequestDTO outfit, @AuthenticationPrincipal UserDetails userDetails) {
		this.outfitService.updateOutfit(outfit, userDetails);
	}

	@PutMapping("editgarments")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateOutfitGarments(@Valid @RequestBody OutfitGarmentUpdateRequestDTO outfit, @AuthenticationPrincipal UserDetails userDetails) {
		this.outfitService.editGarments(outfit, userDetails);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeOutfit(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
		this.outfitService.deleteOutfit(id, userDetails);
	}
}
