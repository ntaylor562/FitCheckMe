package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.controllers.GarmentController;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.GarmentService;

import jakarta.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(GarmentController.class)
public class GarmentControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private GarmentService garmentService;

	private User user;
	private Tag tag1;
	private Garment garment1;

	@BeforeEach
	public void setup() {
		this.user = Mockito.spy(new User("test_user", "test bio"));
		this.tag1 = Mockito.spy(new Tag("tag 1"));
		this.garment1 = Mockito.spy(new Garment("garment 1", user, List.of("url1"), List.of(tag1)));

		Mockito.when(this.user.getId()).thenReturn(1);
		Mockito.when(this.tag1.getId()).thenReturn(1);
		Mockito.when(this.garment1.getId()).thenReturn(1);

		GarmentRequestDTO garment1DTO = GarmentRequestDTO.toDTO(garment1);
		Mockito.when(garmentService.getById(garment1.getId())).thenReturn(garment1DTO);
	}

	@Test
	public void testGetGarment() throws Exception {
		//Testing get garment call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/{id}", this.garment1.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.garmentId").value(this.garment1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.garmentName").value(this.garment1.getName()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(this.garment1.getUser().getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.urls.length()").value(this.garment1.getURLs().size()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.garmentTags.length()").value(this.garment1.getTags().size()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.garmentTags[0].tagName").value(this.garment1.getTags().get(0).getName()));

		//Testing get garment call with bad ID
		Mockito.when(garmentService.getById(2)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/{id}", 2))
			.andExpect(MockMvcResultMatchers.status().isNotFound());

		//Testing get garment call without ID
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment"))
			.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
	}

	@Test
	public void testGetUserGarments() throws Exception {
		// Testing get user garment call is OK
		List<GarmentRequestDTO> userGarments = List.of(GarmentRequestDTO.toDTO(garment1));
		Mockito.when(garmentService.getUserGarments(user.getId())).thenReturn(userGarments);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/usergarments?userId={userId}", user.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));

		// Testing get user garment call is not found
		Mockito.when(garmentService.getUserGarments(3)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/usergarments?userId={userId}", 3))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testCreateGarment() throws Exception {
		GarmentCreateRequestDTO requestDTO = GarmentCreateRequestDTO.toDTO(this.garment1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the create garment call is OK
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isCreated());
		
		//Testing the create garment call with no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		//Testing the create garment call fails with illegal arguments
		Mockito.when(garmentService.createGarment(any(GarmentCreateRequestDTO.class))).thenThrow(IllegalArgumentException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		//Testing the create garment call fails with entity not found
		Mockito.when(garmentService.createGarment(any(GarmentCreateRequestDTO.class))).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateGarment() throws Exception {
		GarmentUpdateRequestDTO requestDTO = new GarmentUpdateRequestDTO(this.garment1.getId(), this.garment1.getName());
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the update garment call is OK
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the update garment call with no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		//Testing the update garment call with entity not found
		Mockito.when(garmentService.updateGarment(any(GarmentUpdateRequestDTO.class))).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
		
		//Testing the update garment call with entity not found
		Mockito.when(garmentService.updateGarment(any(GarmentUpdateRequestDTO.class))).thenThrow(IllegalArgumentException.class);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testDeleteGarment() throws Exception {
		//Testing remove garment call is OK
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment/{id}", this.garment1.getId()))
			.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing remove garment call with no ID
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment"))
			.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

		// Testing remove garment with bad ID
		Mockito.doThrow(IllegalArgumentException.class).when(garmentService).deleteGarment(anyInt());
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment/{id}", this.garment1.getId()))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing remove garment with bad ID
		Mockito.doThrow(EntityNotFoundException.class).when(garmentService).deleteGarment(anyInt());
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment/{id}", this.garment1.getId()))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}