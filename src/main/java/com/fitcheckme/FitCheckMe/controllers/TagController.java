package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.services.TagService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tag")
@CrossOrigin(origins = "*")
public class TagController {
	@Autowired
	private TagService tagService;

	@GetMapping("")
	public List<Tag> findAll() {
		return this.tagService.getAll();
	}

	@GetMapping("{id}")
	public Tag findById(@PathVariable Integer id) {
		try {
			return this.tagService.getById(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of tag not found");
		}
	}

	@GetMapping("{tagName}")
	public Tag findByTagName(@PathVariable String tagName) {
		try {
			return this.tagService.getByTagName(tagName);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag name not found");
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
