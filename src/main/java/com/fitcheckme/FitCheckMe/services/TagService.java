package com.fitcheckme.FitCheckMe.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TagService {
	@Autowired
	private TagRepository tagRepository;

	public List<Tag> getAll() {
		return tagRepository.findAll();
	}

	public Tag getById(Integer id) {
		return tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with ID: %s", String.valueOf(id))));
	}

	public List<Tag> getById(List<Integer> ids) {
		if(ids.isEmpty()) {
			return new ArrayList<Tag>();
		}
		
		List<Tag> res = tagRepository.findAllById(ids);
		
		//If the db result doesn't have as many records as the input, we're missing one or more records
		if(res.size() != ids.size()) {
			throw new EntityNotFoundException(String.format("%d/%d tags in list not found", ids.size() - res.size(), ids.size()));
		}

		return res;
	}

	//TODO add auth so only admins can create tags
	public Tag createTag(TagCreateRequestDTO tag) {
		if(tagRepository.existsByTagNameIgnoreCase(tag.tagName())) {
			throw new DataIntegrityViolationException(String.format("Tagname '%s' is already used", tag.tagName()));
		}
		Tag newTag = new Tag(tag.tagName().toLowerCase());
		this.tagRepository.save(newTag);
		return newTag;
	}

	public Tag getByTagName(String tagName) {
		return this.tagRepository.findByTagNameIgnoreCase(tagName).orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with name: %s", String.valueOf(tagName))));
	}
}
