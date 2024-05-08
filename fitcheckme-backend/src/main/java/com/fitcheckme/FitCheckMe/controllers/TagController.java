package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.services.TagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tag")
public class TagController {
	private final TagService tagService;

	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping("all")
	public List<TagRequestDTO> getAll() {
		return this.tagService.getAll();
	}

	@GetMapping("")
	public ResponseEntity<?> getTag(@RequestParam(required = false) Integer id, @RequestParam(required = false) String name) {
		if(id != null) {
			return new ResponseEntity<>(this.tagService.getById(id), HttpStatus.OK);
		}
		if(name != null) {
			return new ResponseEntity<>(this.tagService.getByTagName(name), HttpStatus.OK);
		}
		return new ResponseEntity<>("No ID or name provided", HttpStatus.BAD_REQUEST);
	}

	@PostMapping("")
	public ResponseEntity<String> createTag(@Valid @RequestBody TagCreateRequestDTO tag) {
		try {
			tagService.createTag(tag);
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		catch(DataIntegrityViolationException e) {
			return new ResponseEntity<>(String.format("Tag with name %s already exists", tag.tagName()), HttpStatus.CONFLICT);
		}
	}
}
