package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.services.TagService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tag")
@CrossOrigin(origins = "*")
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
	public TagRequestDTO findByTagName(@RequestParam(required = false) Integer id, @RequestParam(required = false) String name) {
		try {
			if(id != null) {
				return this.tagService.getById(id);
			}
			if(name != null) {
				return this.tagService.getByTagName(name);
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ID or name provided");
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found");
		}
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createTag(@Valid @RequestBody TagCreateRequestDTO tag) {
		try {
			tagService.createTag(tag);
		}
		catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Tag with name %s already exists", tag.tagName()));
		}
	}
}
