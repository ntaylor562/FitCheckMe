package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.ExceptionResponseDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.services.TagService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tag")
public class TagController {
	private final TagService tagService;

	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping("all")
	public ResponseEntity<List<TagRequestDTO>> getAll() {
		return new ResponseEntity<List<TagRequestDTO>>(this.tagService.getAll(), HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<?> getTag(@RequestParam(required = false) Integer id, @RequestParam(required = false) String name) {
		if(id != null) {
			return new ResponseEntity<TagRequestDTO>(this.tagService.getById(id), HttpStatus.OK);
		}
		if(name != null) {
			return new ResponseEntity<TagRequestDTO>(this.tagService.getByTagName(name), HttpStatus.OK);
		}
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("No ID or name provided", "An ID or a tag name is required"), HttpStatus.BAD_REQUEST);
	}

	@PostMapping("create")
	public ResponseEntity<?> createTag(@Valid @RequestBody TagCreateRequestDTO tag) {
		try {
			return new ResponseEntity<TagRequestDTO>(tagService.createTag(tag), HttpStatus.CREATED);
		}
		catch(DataIntegrityViolationException e) {
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Tagname already exists", String.format("Tag with name %s already exists", tag.tagName())), HttpStatus.CONFLICT);
		}
	}

	@DeleteMapping("delete")
	public ResponseEntity<?> deleteTag(@RequestParam Integer id) {
		try {
			tagService.deleteTag(id);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch(EntityNotFoundException e) {
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Tag not found", String.format("Tag not found with ID: %d", id)), HttpStatus.NOT_FOUND);
		}
	}
}
