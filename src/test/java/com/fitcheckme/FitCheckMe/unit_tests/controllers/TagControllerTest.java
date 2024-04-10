package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TagControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TagService tagService;

	private Tag tag1;

	@BeforeEach
	public void setup() {
		this.tag1 = Mockito.spy(new Tag("test tag 1"));
		Mockito.when(this.tag1.getId()).thenReturn(1);

		List<TagRequestDTO> tagList = List.of(TagRequestDTO.toDTO(tag1));
		Mockito.when(tagService.getAll()).thenReturn(tagList);

		TagRequestDTO tagRequestDTO = TagRequestDTO.toDTO(tag1);
		Mockito.when(tagService.getById(1)).thenReturn(tagRequestDTO);
		Mockito.when(tagService.getByTagName(this.tag1.getName())).thenReturn(tagRequestDTO);
	}

	@Test
	public void testGetTag() throws Exception {

		//Testing get all
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag/all"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].tagId").isNumber())
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].tagName").isString());

		//Testing get by ID
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?id={id}", 1))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagId").value(tag1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value(tag1.getName()));

		//Testing get by tag name
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?name={name}", "test tag 1"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagId").value(tag1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value(tag1.getName()));
		
		//Testing get tag has error when no ID or name specified
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		//Testing handling no ID found
		Mockito.when(tagService.getById(5)).thenThrow(new EntityNotFoundException("Tag not found"));
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?id={id}", 5))
			.andExpect(MockMvcResultMatchers.status().isNotFound());

		//Testing handling no tag name found
		Mockito.when(tagService.getByTagName("sample tag name")).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/tag?name={name}", "sample tag name"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testCreateTag() throws Exception {
		TagCreateRequestDTO requestDTO = TagCreateRequestDTO.toDTO(this.tag1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing creating tag
		mockMvc.perform(MockMvcRequestBuilders.post("/api/tag")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isCreated());

		//Testing create tag call with no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/tag"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		//Testing create tag fails with conflict
		Mockito.when(tagService.createTag(any(TagCreateRequestDTO.class))).thenThrow(DataIntegrityViolationException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/tag")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isConflict());
	}
}
