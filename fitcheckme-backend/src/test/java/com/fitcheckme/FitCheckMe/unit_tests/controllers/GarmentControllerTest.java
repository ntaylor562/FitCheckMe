package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.controllers.GarmentController;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.GarmentService;

import jakarta.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(GarmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GarmentControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private GarmentService garmentService;

	private User user;
	private Tag tag1;
	private Garment garment1;
	private Garment garment2;

	@BeforeEach
	public void setup() {
		this.user = Mockito.spy(new User("test_user", "test@email.com", "pass", "test bio", Set.of()));
		this.tag1 = Mockito.spy(new Tag("tag 1"));
		this.garment1 = Mockito.spy(new Garment(user, "garment 1", List.of("url1"), List.of(tag1)));
		this.garment2 = Mockito.spy(new Garment(user, "garment 2", List.of("url2"), List.of(tag1)));

		Mockito.when(this.user.getId()).thenReturn(1);
		Mockito.when(this.tag1.getId()).thenReturn(1);
		Mockito.when(this.garment1.getId()).thenReturn(1);
		Mockito.when(this.garment2.getId()).thenReturn(2);

		UserDetails userDetails = new CustomUserDetails(user.getId(),
			user.getUsername(),
			user.getPassword(),
			user.getRoles());
		Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        // Set up the SecurityContextHolder with the mock Authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

		GarmentRequestDTO garment1DTO = GarmentRequestDTO.toDTO(garment1);
		GarmentRequestDTO garment2DTO = GarmentRequestDTO.toDTO(garment1);
		Mockito.when(garmentService.getById(garment1.getId())).thenReturn(garment1DTO);
		Mockito.when(garmentService.getById(garment2.getId())).thenReturn(garment2DTO);
	}

	@Test
	public void testGetAllGarments() throws Exception {
		// Testing get all garments call is OK
		List<GarmentRequestDTO> garmentList = List.of(GarmentRequestDTO.toDTO(garment1),
				GarmentRequestDTO.toDTO(garment2));
		Mockito.when(garmentService.getAll()).thenReturn(garmentList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/all"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetGarment() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment?id={id}", this.garment1.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.garmentId").value(this.garment1.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.garmentName").value(this.garment1.getName()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(this.garment1.getUser().getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.urls.length()").value(this.garment1.getURLs().size()))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.garmentTags.length()").value(this.garment1.getTags().size()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.garmentTags[0].tagName")
						.value(this.garment1.getTags().iterator().next().getName()));
	}

	@Test
	public void testGetGarmentAndExpectEntityNotFoundException() throws Exception {
		// Testing get garment call with bad ID
		Mockito.when(garmentService.getById(3)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment?id={id}", 3))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetGarmentGivenBadId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment?id={id}", "test"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testGetGarmentGivenNoId() throws Exception {
		// Testing get garment call without ID
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testGetUserGarments() throws Exception {
		List<GarmentRequestDTO> userGarments = List.of(GarmentRequestDTO.toDTO(garment1));
		Mockito.when(garmentService.getUserGarments(user.getId())).thenReturn(userGarments);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/usergarments?userId={userId}", user.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
	}

	@Test
	public void testGetUserGarmentsAndExpectEntityNotFoundException() throws Exception {
		Mockito.when(garmentService.getUserGarments(3)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/usergarments?userId={userId}", 3))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetUserGarmentsGivenNoId() throws Exception {
		// Testing get user garment call with no user ID (to get the currently logged in
		// user's garments)
		List<GarmentRequestDTO> userGarments = List.of(GarmentRequestDTO.toDTO(garment1));
		Mockito.when(garmentService.getUserGarments(user.getId())).thenReturn(userGarments);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/garment/usergarments"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1));
	}

	@Test
	public void testCreateGarment() throws Exception {
		GarmentCreateRequestDTO requestDTO = GarmentCreateRequestDTO.toDTO(this.garment1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void testCreateGarmentWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment/create"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateGarmentAndExpectIllegalArgumentException() throws Exception {
		GarmentCreateRequestDTO requestDTO = GarmentCreateRequestDTO.toDTO(this.garment1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.when(garmentService.createGarment(any(GarmentCreateRequestDTO.class), any(CustomUserDetails.class)))
				.thenThrow(IllegalArgumentException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateGarmentAndExpectEntityNotFoundException() throws Exception {
		// Testing the create garment call fails with entity not found
		GarmentCreateRequestDTO requestDTO = GarmentCreateRequestDTO.toDTO(this.garment1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.when(garmentService.createGarment(any(GarmentCreateRequestDTO.class), any(CustomUserDetails.class)))
				.thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/garment/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateGarment() throws Exception {
		GarmentUpdateRequestDTO requestDTO = new GarmentUpdateRequestDTO(this.garment1.getId(),
				this.garment1.getName(), Set.of(), Set.of(), Set.of(), Set.of());
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment/edit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testUpdateGarmentWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment/edit"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdateGarmentAndExpectEntityNotFoundException() throws Exception {
		GarmentUpdateRequestDTO requestDTO = new GarmentUpdateRequestDTO(this.garment1.getId(),
				this.garment1.getName(), Set.of(), Set.of(), Set.of(), Set.of());
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.when(garmentService.updateGarment(any(GarmentUpdateRequestDTO.class), any(CustomUserDetails.class)))
				.thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment/edit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateGarmentAndExpectIllegalArgumentException() throws Exception {
		GarmentUpdateRequestDTO requestDTO = new GarmentUpdateRequestDTO(this.garment1.getId(),
				this.garment1.getName(), Set.of(), Set.of(), Set.of(), Set.of());
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.when(garmentService.updateGarment(any(GarmentUpdateRequestDTO.class), any(CustomUserDetails.class)))
				.thenThrow(IllegalArgumentException.class);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/garment/edit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testDeleteGarment() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment?id={id}", this.garment1.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testDeleteGarmentGivenNoId() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testDeleteGarmentGivenBadId() throws Exception {
		Mockito.doThrow(IllegalArgumentException.class).when(garmentService).deleteGarment(anyInt(),
				any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment?id={id}", this.garment1.getId()))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testDeleteGarmentAndExpectEntityNotFoundException() throws Exception {
		Mockito.doThrow(EntityNotFoundException.class).when(garmentService).deleteGarment(anyInt(),
				any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/garment?id={id}", this.garment1.getId()))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}
