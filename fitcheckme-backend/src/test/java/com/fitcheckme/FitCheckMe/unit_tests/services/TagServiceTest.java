package com.fitcheckme.FitCheckMe.unit_tests.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.services.TagService;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
	@InjectMocks
	private TagService tagService;
	
	@Mock
	private TagRepository tagRepository;

	@BeforeEach
	public void setup() {

	}

	@Test
	public void testGetAll() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Tag tag2 = Mockito.spy(new Tag("tag2"));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(tag2.getId()).thenReturn(2);
		
		Mockito.when(tagRepository.findAllByOrderByIdAsc()).thenReturn(List.of(tag1, tag2));
		List<TagRequestDTO> result = tagService.getAll();
		assertThat(result).hasSize(2);
		assertThat(result).allMatch(tag -> tag.getClass().equals(TagRequestDTO.class));
	}
}
