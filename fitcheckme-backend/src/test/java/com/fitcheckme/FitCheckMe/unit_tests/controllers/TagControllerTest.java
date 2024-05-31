package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.controllers.TagController;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.services.TagService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TagControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TagService tagService;

	private Tag tag1;
	private Tag tag2;

	@BeforeEach
	public void setup() {
		this.tag1 = Mockito.spy(new Tag("test tag 1"));
		this.tag2 = Mockito.spy(new Tag("test tag 2"));
		Mockito.when(this.tag1.getId()).thenReturn(1);
		Mockito.when(this.tag2.getId()).thenReturn(2);

		TagRequestDTO tagRequestDTO1 = TagRequestDTO.toDTO(tag1);
		Mockito.when(tagService.getById(1)).thenReturn(tagRequestDTO1);
		Mockito.when(tagService.getByTagName(this.tag1.getName())).thenReturn(tagRequestDTO1);
		TagRequestDTO tagRequestDTO2 = TagRequestDTO.toDTO(tag2);
		Mockito.when(tagService.getById(2)).thenReturn(tagRequestDTO2);
		Mockito.when(tagService.getByTagName(this.tag2.getName())).thenReturn(tagRequestDTO2);
	}

	@Test
	public void testGetAllTags() throws Exception {
		List<TagRequestDTO> tagList = List.of(TagRequestDTO.toDTO(tag1), TagRequestDTO.toDTO(tag2));
		Mockito.when(tagService.getAll()).thenReturn(tagList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag/all"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].tagId").isNumber())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].tagName").isString())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetTagById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?id={id}", 1))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagId").value(tag1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value(tag1.getName()));
	}

	@Test
	public void testGetTagByName() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?name={name}", "test tag 1"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagId").value(tag1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value(tag1.getName()));
	}

	@Test
	public void testGetTagWithNoIdOrName() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testGetTagWithInvalidId() throws Exception {
		//Testing handling no ID found
		Mockito.when(tagService.getById(5)).thenThrow(new EntityNotFoundException("Tag not found"));
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?id={id}", 5))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetTagWithInvalidName() throws Exception {
		//Testing handling no tag name found
		Mockito.when(tagService.getByTagName("sample tag name")).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?name={name}", "sample tag name"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testCreateTag() throws Exception {
		TagCreateRequestDTO createRequestDTO = TagCreateRequestDTO.toDTO(this.tag1);
		TagRequestDTO requestDTO = TagRequestDTO.toDTO(this.tag1);
		String requestBody = new ObjectMapper().writeValueAsString(createRequestDTO);
		Mockito.when(tagService.createTag(any(TagCreateRequestDTO.class))).thenReturn(requestDTO);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/tag/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagId").value(tag1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value(tag1.getName()));
	}

	@Test
	public void testCreateTagWithNoBody() throws Exception {
		//Testing create tag call with no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/tag/create"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateTagWithConflictingName() throws Exception {
		//Testing create tag fails with conflict
		TagCreateRequestDTO requestDTO = TagCreateRequestDTO.toDTO(this.tag1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.when(tagService.createTag(any(TagCreateRequestDTO.class))).thenThrow(DataIntegrityViolationException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/tag/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isConflict());
	}

	@Test
	public void testDeleteTag() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/tag/delete?id={id}", 1))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testDeleteTagWithNoId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/tag/delete"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testDeleteTagAndExpectEntityNotFoundException() throws Exception {
		Mockito.doThrow(EntityNotFoundException.class).when(tagService).deleteTag(1);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/tag/delete?id={id}", 1))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}
