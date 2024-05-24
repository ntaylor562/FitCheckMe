package com.fitcheckme.FitCheckMe.integration_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;

public class TagControllerIntegrationTest extends AbstractIntegrationTest {
	@Autowired
	private TagRepository tagRepository;

	@Test
	@Rollback
	public void testGetAllTags() {
		Tag tag1 = tagRepository.save(new Tag("tag1"));
		Tag tag2 = tagRepository.save(new Tag("tag2"));
		
		ResponseEntity<List<Object>> response = restTemplate.exchange("/api/tag/all", HttpMethod.GET, null, getTypeOfListOfType(Object.class));
		List<TagRequestDTO> tags = Optional.of(response.getBody()).get().stream().map(tag -> mapper.convertValue(tag, TagRequestDTO.class)).toList();
		assertThat(tags).containsExactly(TagRequestDTO.toDTO(tag1), TagRequestDTO.toDTO(tag2));
	}
}
