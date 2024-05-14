package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.services.OutfitService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/outfit")
public class OutfitController {
	private final OutfitService outfitService;

	public OutfitController(OutfitService outfitService) {
		this.outfitService = outfitService;
	}

	@GetMapping("all")
	public ResponseEntity<List<OutfitRequestDTO>> getAll() {
		return new ResponseEntity<List<OutfitRequestDTO>>(this.outfitService.getAll(), HttpStatus.OK);
	}

	@GetMapping("{id}")
	public ResponseEntity<OutfitRequestDTO> getById(@PathVariable Integer id) {
		return new ResponseEntity<OutfitRequestDTO>(this.outfitService.getById(id), HttpStatus.OK);
	}

	@GetMapping("useroutfits")
	public ResponseEntity<List<OutfitRequestDTO>> getUserOutfits(@RequestParam(required = false) Integer userId, @AuthenticationPrincipal UserDetails userDetails) {
		if(userId == null) {
			return new ResponseEntity<List<OutfitRequestDTO>>(this.outfitService.getUserOutfits(userDetails.getUsername()), HttpStatus.OK);
		}
		return new ResponseEntity<List<OutfitRequestDTO>>(this.outfitService.getUserOutfits(userId), HttpStatus.OK);
	}
	

	@PostMapping("create")
	public ResponseEntity<OutfitRequestDTO> createOutfit(@Valid @RequestBody OutfitCreateRequestDTO outfit, @AuthenticationPrincipal UserDetails userDetails) {
		return new ResponseEntity<OutfitRequestDTO>(this.outfitService.createOutfit(outfit, userDetails), HttpStatus.CREATED);
	}

	@PutMapping("")
	public ResponseEntity<OutfitRequestDTO> updateOutfit(@Valid @RequestBody OutfitUpdateRequestDTO outfit, @AuthenticationPrincipal UserDetails userDetails) {
		return new ResponseEntity<OutfitRequestDTO>(this.outfitService.updateOutfit(outfit, userDetails), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void removeOutfit(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
		this.outfitService.deleteOutfit(id, userDetails);
	}
}
