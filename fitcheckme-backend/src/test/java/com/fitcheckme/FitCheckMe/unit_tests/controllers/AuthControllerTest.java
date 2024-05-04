package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.auth.JwtAuthorizationFilter;
import com.fitcheckme.FitCheckMe.controllers.AuthController;
import com.fitcheckme.FitCheckMe.services.AuthService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private JwtAuthorizationFilter jwtAuthorizationFilter;

	@BeforeEach
	public void setup() {

	}

	@Test
	public void testLogin() throws Exception {
		UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test", "test");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);


		//Testing with valid credentials
		Mockito.when(authService.userLogin(requestDTO)).thenReturn(new UserLoginReturnDTO(requestDTO.username(), "token"));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.cookie().exists("jwt-token"))
			.andExpect(MockMvcResultMatchers.cookie().httpOnly("jwt-token", true))
			.andExpect(MockMvcResultMatchers.cookie().secure("jwt-token", true))
			.andExpect(MockMvcResultMatchers.cookie().path("jwt-token", "/"));

		//Testing with no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());

		//Testing with invalid credentials
		UserLoginRequestDTO invalidRequestDTO = new UserLoginRequestDTO("invalid", "invalid");
		String invalidRequestBody = new ObjectMapper().writeValueAsString(invalidRequestDTO);
		Mockito.when(authService.userLogin(invalidRequestDTO)).thenThrow(BadCredentialsException.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(invalidRequestBody))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testLogout() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.cookie().exists("jwt-token"))
			.andExpect(MockMvcResultMatchers.cookie().httpOnly("jwt-token", true))
			.andExpect(MockMvcResultMatchers.cookie().secure("jwt-token", true))
			.andExpect(MockMvcResultMatchers.cookie().path("jwt-token", "/"))
			.andExpect(MockMvcResultMatchers.cookie().maxAge("jwt-token", 0))
			.andExpect(MockMvcResultMatchers.cookie().value("jwt-token", ""));
	}
}