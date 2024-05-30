package com.fitcheckme.FitCheckMe.integration_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;

public class TagControllerIntegrationTest extends AbstractIntegrationTest {
	@Autowired
	private TagRepository tagRepository;

	@BeforeEach
	public void wipeDatabase() {
		tagRepository.deleteAll();
	}

	@Test
	@Rollback
	public void testGetAllTagsAndExpectEmptyList() {
		ResponseEntity<Object> response = getCall("/api/tag/all");
		List<TagRequestDTO> tags = getListOfObjectsFromResponse(response, TagRequestDTO.class);
		assertThat(tags).isEmpty();
	}

	@Test
	@Rollback
	public void testGetAllTags() {
		Tag tag1 = tagRepository.save(new Tag("tag1"));
		Tag tag2 = tagRepository.save(new Tag("tag2"));

		ResponseEntity<Object> response = getCall("/api/tag/all");
		List<TagRequestDTO> tags = getListOfObjectsFromResponse(response, TagRequestDTO.class);
		assertThat(tags).containsExactly(TagRequestDTO.toDTO(tag1), TagRequestDTO.toDTO(tag2));
	}
}
