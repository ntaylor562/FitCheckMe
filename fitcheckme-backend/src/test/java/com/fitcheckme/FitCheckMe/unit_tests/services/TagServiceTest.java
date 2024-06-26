package com.fitcheckme.FitCheckMe.unit_tests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.services.TagService;

import jakarta.persistence.EntityNotFoundException;

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
	public void testGetAllAndExpectListOfTags() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Tag tag2 = Mockito.spy(new Tag("tag2"));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(tag2.getId()).thenReturn(2);

		Mockito.when(tagRepository.findAllByOrderByIdAsc()).thenReturn(List.of(tag1, tag2));
		List<TagRequestDTO> result = tagService.getAll();
		assertThat(result).hasSize(2);
		assertThat(result).allMatch(tag -> tag.getClass().equals(TagRequestDTO.class));

		Mockito.verify(tagRepository, Mockito.times(1)).findAllByOrderByIdAsc();
	}

	@Test
	public void testGetAllAndExpectEmptyList() {
		Mockito.when(tagRepository.findAllByOrderByIdAsc()).thenReturn(List.of());
		List<TagRequestDTO> result = tagService.getAll();
		assertThat(result).isEmpty();

		Mockito.verify(tagRepository, Mockito.times(1)).findAllByOrderByIdAsc();
	}

	@Test
	public void testGetByIdAndExpectTag() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Mockito.when(tag1.getId()).thenReturn(1);

		Mockito.when(tagRepository.findById(1)).thenReturn(Optional.of(tag1));
		TagRequestDTO result = tagService.getById(1);
		assertThat(result).isNotNull()
				.isEqualTo(TagRequestDTO.toDTO(tag1));

		Mockito.verify(tagRepository, Mockito.times(1)).findById(any());
	}

	@Test
	public void testGetByIdAndExpectEntityNotFoundException() {
		Mockito.when(tagRepository.findById(2)).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> tagService.getById(2))
				.withMessage("Tag not found with ID: 2");

		Mockito.verify(tagRepository, Mockito.times(1)).findById(any());
	}

	@Test
	public void givenListOfIds_whenGetById_thenReturnTags() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Tag tag2 = Mockito.spy(new Tag("tag2"));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(tag2.getId()).thenReturn(2);

		Mockito.when(tagRepository.findAllById(List.of(1, 2))).thenReturn(List.of(tag1, tag2));
		List<TagRequestDTO> result = tagService.getById(List.of(1, 2));
		assertThat(result).hasSize(2);
		assertThat(result).allMatch(tag -> tag.getClass().equals(TagRequestDTO.class));

		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
	}

	@Test
	public void givenListOfIdsWithNonExistingTag_whenGetById_thenThrowEntityNotFoundException() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Mockito.when(tag1.getId()).thenReturn(1);

		Mockito.when(tagRepository.findAllById(List.of(1, 2))).thenReturn(List.of(tag1));
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> tagService.getById(List.of(1, 2)))
				.withMessage("Tags not found with IDs: [2]");

		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
	}

	@Test
	public void testGetByTagNameAndExpectTag() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Mockito.when(tag1.getId()).thenReturn(1);

		Mockito.when(tagRepository.findByTagNameIgnoreCase("tag1")).thenReturn(Optional.of(tag1));
		TagRequestDTO result = tagService.getByTagName("tag1");
		assertThat(result).isNotNull()
				.isEqualTo(TagRequestDTO.toDTO(tag1));

		Mockito.verify(tagRepository, Mockito.times(1)).findByTagNameIgnoreCase(any());
	}

	@Test
	public void testGetByTagNameAndExpectEntityNotFoundException() {
		Mockito.when(tagRepository.findByTagNameIgnoreCase("tag2")).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> tagService.getByTagName("tag2"))
				.withMessage("Tag not found with name: tag2");

		Mockito.verify(tagRepository, Mockito.times(1)).findByTagNameIgnoreCase(any());
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testCreateTagAndExpectTag() {
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Mockito.when(tag1.getId()).thenReturn(1);

		Mockito.when(tagRepository.existsByTagNameIgnoreCase("tag1")).thenReturn(false);
		Mockito.when(tagRepository.save(Mockito.any(Tag.class))).thenReturn(tag1);
		TagRequestDTO result = tagService.createTag(new TagCreateRequestDTO("tag1"));
		assertThat(result).isNotNull()
				.isEqualTo(TagRequestDTO.toDTO(tag1));

		Mockito.verify(tagRepository, Mockito.times(1)).existsByTagNameIgnoreCase(any());
		Mockito.verify(tagRepository, Mockito.times(1)).save(any());
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testCreateTagAndExpectDataIntegrityViolationException() {
		Mockito.when(tagRepository.existsByTagNameIgnoreCase("tag1")).thenReturn(true);
		assertThatExceptionOfType(Exception.class)
				.isThrownBy(() -> tagService.createTag(new TagCreateRequestDTO("tag1")))
				.withMessage("Tag already exists with name: tag1");

		Mockito.verify(tagRepository, Mockito.times(1)).existsByTagNameIgnoreCase(any());
		Mockito.verify(tagRepository, Mockito.times(0)).save(any());
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testDeleteTag() {
		Mockito.when(tagRepository.existsById(1)).thenReturn(true);

		assertThatNoException().isThrownBy(() -> tagService.deleteTag(1));

		Mockito.verify(tagRepository, Mockito.times(1)).deleteGarmentTags(any());
		Mockito.verify(tagRepository, Mockito.times(1)).deleteOutfitTags(any());
		Mockito.verify(tagRepository, Mockito.times(1)).deleteById(any());
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testDeleteTagAndExpectEntityNotFoundException() {
		Mockito.when(tagRepository.existsById(2)).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> tagService.deleteTag(2))
				.withMessage("Tag not found with ID: 2");

		Mockito.verify(tagRepository, Mockito.never()).deleteGarmentTags(any());
		Mockito.verify(tagRepository, Mockito.never()).deleteOutfitTags(any());
		Mockito.verify(tagRepository, Mockito.never()).deleteById(any());
	}
}
