package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitGarmentUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetailsService;
import com.fitcheckme.FitCheckMe.auth.JwtUtil;
import com.fitcheckme.FitCheckMe.controllers.OutfitController;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.OutfitService;

import jakarta.persistence.EntityNotFoundException;

import com.fitcheckme.FitCheckMe.models.Tag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

@WebMvcTest(OutfitController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OutfitControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OutfitService outfitService;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private CustomUserDetailsService userDetailsService;

	private User user;
	private Tag tag1;
	private Tag tag2;
	private Garment garment1;
	private Garment garment2;
	private Outfit outfit1;
	private Outfit outfit2;

	@BeforeEach
	public void setup() {
		this.user = Mockito.spy(new User("test_username", "test@email.com", "pass", "test bio"));
		this.tag1 = Mockito.spy(new Tag("tag1"));
		this.tag2 = Mockito.spy(new Tag("tag1"));
		this.garment1 = Mockito.spy(new Garment("garment_1", user, Arrays.asList("url1"), Arrays.asList(tag1)));
		this.garment2 = Mockito.spy(new Garment("garment_2", user, Arrays.asList("url2"), Arrays.asList(tag2)));
		LocalDateTime now = LocalDateTime.of(2024, 1, 30, 12, 13, 14);
		this.outfit1 = Mockito.spy(new Outfit(user, "test_outfit1", "test description 1", now, new HashSet<>(Arrays.asList(garment1)), Arrays.asList(tag1)));
		this.outfit2 = Mockito.spy(new Outfit(user, "test_outfit2", "test description 2", now, new HashSet<>(Arrays.asList(garment1, garment2)), Arrays.asList(tag2)));

		Mockito.when(this.user.getId()).thenReturn(1);
		Mockito.when(this.tag1.getId()).thenReturn(1);
		Mockito.when(this.tag2.getId()).thenReturn(2);
		Mockito.when(this.garment1.getId()).thenReturn(1);
		Mockito.when(this.garment2.getId()).thenReturn(2);
		Mockito.when(this.outfit1.getId()).thenReturn(1);
		Mockito.when(this.outfit2.getId()).thenReturn(2);
		Mockito.when(this.outfit1.getUser()).thenReturn(user);
		Mockito.when(this.outfit2.getUser()).thenReturn(user);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(user.getUsername())
			.password("")
			.build();
		Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        // Set up the SecurityContextHolder with the mock Authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

		OutfitRequestDTO outfit1DTO = OutfitRequestDTO.toDTO(this.outfit1);
		Mockito.when(outfitService.getById(1)).thenReturn(outfit1DTO);
		Mockito.when(outfitService.getById(2)).thenThrow(EntityNotFoundException.class);
		List<OutfitRequestDTO> userOutfits = List.of(OutfitRequestDTO.toDTO(outfit1), OutfitRequestDTO.toDTO(outfit2));
		Mockito.when(outfitService.getUserOutfits(1)).thenReturn(userOutfits);
	}

	@Test
	public void testGetAllOutfits() throws Exception {
		//Testing the get all outfits call is OK
		List<OutfitRequestDTO> outfitList = List.of(OutfitRequestDTO.toDTO(outfit1), OutfitRequestDTO.toDTO(outfit2));
		Mockito.when(outfitService.getAll()).thenReturn(outfitList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/outfit/all"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetOutfitById() throws Exception {
		//Testing the get outfit by id with 1 id call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/outfit/{id}", outfit1.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.outfitId").value(outfit1.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(outfit1.getUser().getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.outfitName").value(outfit1.getName()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.outfitDesc").value(outfit1.getDesc()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.creationDate").value(outfit1.getCreationDate().toString()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.garments[0].garmentId").value(outfit1.getGarments().iterator().next().getId()));

		//Testing the get outfit by id call is not found
		Mockito.when(outfitService.getById(3)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/outfit/{id}", 2))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetUserOutfits() throws Exception {
		// Testing get user outfits call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/outfit/useroutfits?userId={userId}", user.getId()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
				
		// Testing get user outfits call is not found
		Mockito.when(outfitService.getUserOutfits(3)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/outfit/useroutfits?userId={userId}", 3))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testCreateOutfit() throws Exception {
		OutfitCreateRequestDTO requestDTO = OutfitCreateRequestDTO.toDTO(outfit1);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the create outfit call is OK
		mockMvc.perform(MockMvcRequestBuilders.post("/api/outfit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isCreated());
		
		//Testing the create outfit call with no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/outfit"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		//Testing the create outfit call fails with illegal arguments
		Mockito.when(outfitService.createOutfit(any(OutfitCreateRequestDTO.class), any(UserDetails.class))).thenThrow(IllegalArgumentException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/outfit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		//Testing the create outfit call fails with entity not found
		Mockito.when(outfitService.createOutfit(any(OutfitCreateRequestDTO.class), any(UserDetails.class))).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/outfit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateOutfit() throws Exception {
		OutfitUpdateRequestDTO requestDTO = new OutfitUpdateRequestDTO(outfit1.getId(), outfit1.getName() + "_updated", outfit1.getDesc() + "_updated");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the update outfit call is OK
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the update outfit call with no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		//Testing the update outfit call with entity not found
		Mockito.when(outfitService.updateOutfit(any(OutfitUpdateRequestDTO.class), any(UserDetails.class))).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
		
		//Testing the update outfit call with illegal arguments
		Mockito.when(outfitService.updateOutfit(any(OutfitUpdateRequestDTO.class), any(UserDetails.class))).thenThrow(IllegalArgumentException.class);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdateOutfitGarments() throws Exception {
		OutfitGarmentUpdateRequestDTO requestDTO = new OutfitGarmentUpdateRequestDTO(outfit1.getId(), Arrays.asList(garment1.getId()), Arrays.asList(garment2.getId()));
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the update outfit garments call is OK
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit/editgarments")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isAccepted());
		
		//Testing the update outfit garments call with no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit/editgarments"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		//Testing the update outfit garments call with entity not found
		Mockito.doThrow(EntityNotFoundException.class).when(outfitService).editGarments(any(OutfitGarmentUpdateRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit/editgarments")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isNotFound());

		//Testing the update outfit garments call with illegal arguments
		Mockito.doThrow(IllegalArgumentException.class).when(outfitService).editGarments(any(OutfitGarmentUpdateRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/outfit/editgarments")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testRemoveOutfit() throws Exception {
		//Testing remove outfit call is OK
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/outfit/{id}", this.outfit1.getId()))
			.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing remove outfit call with no ID
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/outfit"))
			.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

		// Testing remove outfit with bad ID
		Mockito.doThrow(IllegalArgumentException.class).when(outfitService).deleteOutfit(anyInt(), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/outfit/{id}", this.outfit1.getId()))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing remove outfit with bad ID
		Mockito.doThrow(EntityNotFoundException.class).when(outfitService).deleteOutfit(anyInt(), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/outfit/{id}", this.outfit1.getId()))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}
