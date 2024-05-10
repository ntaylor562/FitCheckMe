package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import org.hamcrest.Matchers;
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

import jakarta.servlet.http.Cookie;

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
		Mockito.when(authService.userLogin(requestDTO)).thenReturn(new UserLoginReturnDTO(requestDTO.username(), "token", "refreshToken"));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestBody))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.cookie().exists("jwt-access-token"))
			.andExpect(MockMvcResultMatchers.cookie().httpOnly("jwt-access-token", true))
			.andExpect(MockMvcResultMatchers.cookie().secure("jwt-access-token", true))
			.andExpect(MockMvcResultMatchers.cookie().path("jwt-access-token", "/"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.cookie().exists("jwt-refresh-token"))
			.andExpect(MockMvcResultMatchers.cookie().httpOnly("jwt-refresh-token", true))
			.andExpect(MockMvcResultMatchers.cookie().secure("jwt-refresh-token", true))
			.andExpect(MockMvcResultMatchers.cookie().path("jwt-refresh-token", "/api/auth"));

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
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	public void testLogout() throws Exception {
		//Testing with no cookies
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout"))
			.andExpect(MockMvcResultMatchers.status().isOk());

		//Testing with cookies
		Cookie accessToken = new Cookie("jwt-access-token", "token");
		Cookie refreshToken = new Cookie("jwt-refresh-token", "refreshToken");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
			.cookie(accessToken)
			.cookie(refreshToken))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.cookie().exists("jwt-access-token"))
			.andExpect(MockMvcResultMatchers.cookie().httpOnly("jwt-access-token", true))
			.andExpect(MockMvcResultMatchers.cookie().secure("jwt-access-token", true))
			.andExpect(MockMvcResultMatchers.cookie().path("jwt-access-token", "/"))
			.andExpect(MockMvcResultMatchers.cookie().maxAge("jwt-access-token", 0))
			.andExpect(MockMvcResultMatchers.cookie().value("jwt-access-token", Matchers.equalTo(null)))
			
			.andExpect(MockMvcResultMatchers.cookie().exists("jwt-refresh-token"))
			.andExpect(MockMvcResultMatchers.cookie().httpOnly("jwt-refresh-token", true))
			.andExpect(MockMvcResultMatchers.cookie().secure("jwt-refresh-token", true))
			.andExpect(MockMvcResultMatchers.cookie().path("jwt-refresh-token", "/"))
			.andExpect(MockMvcResultMatchers.cookie().maxAge("jwt-refresh-token", 0))
			.andExpect(MockMvcResultMatchers.cookie().value("jwt-refresh-token", Matchers.equalTo(null)));
	}
}
