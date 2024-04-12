package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TagService {
	private final TagRepository tagRepository;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	public List<TagRequestDTO> getAll() {
		return tagRepository.findAll().stream().map(tag -> TagRequestDTO.toDTO(tag)).toList();
	}

	public TagRequestDTO getById(Integer id) {
		return TagRequestDTO.toDTO(tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id)))));
	}

	public List<TagRequestDTO> getById(List<Integer> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<TagRequestDTO>();
		}
		
		List<Tag> res = tagRepository.findAllById(ids);
		
		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			throw new EntityNotFoundException(String.format("%d/%d tags in list not found", ids.size() - res.size(), ids.size()));
		}

		return res.stream().map(tag -> TagRequestDTO.toDTO(tag)).toList();
	}

	//TODO add auth so only admins can create tags
	public TagRequestDTO createTag(TagCreateRequestDTO tag) {
		if(tagRepository.existsByTagNameIgnoreCase(tag.tagName())) {
			throw new DataIntegrityViolationException(String.format("Tagname '%s' is already used", tag.tagName()));
		}
		Tag newTag = new Tag(tag.tagName().toLowerCase());
		this.tagRepository.save(newTag);
		return TagRequestDTO.toDTO(newTag);
	}

	public TagRequestDTO getByTagName(String tagName) {
		return TagRequestDTO.toDTO(this.tagRepository.findByTagNameIgnoreCase(tagName).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with name: %s", String.valueOf(tagName)))));
	}
}
