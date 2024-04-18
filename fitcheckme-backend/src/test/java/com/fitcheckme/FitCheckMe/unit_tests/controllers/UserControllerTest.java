package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.controllers.UserController;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	private User user;

	@BeforeEach
	public void setUp() {
		this.user = Mockito.spy(new User("test_username", "test bio"));
		Mockito.when(this.user.getId()).thenReturn(1);

		UserRequestDTO userDTO = UserRequestDTO.toDTO(this.user);
		Mockito.when(userService.getById(1)).thenReturn(userDTO);
		Mockito.when(userService.getById(2)).thenThrow(EntityNotFoundException.class);

		Mockito.when(userService.getByUsername("test_username")).thenReturn(userDTO);
		Mockito.when(userService.getByUsername("not_a_user")).thenThrow(EntityNotFoundException.class);
	}

	@Test
	public void testGetUserById() throws Exception {
		//Testing the get user by id call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", user.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user.getBio()));
			
		//Testing the get user by id call is not found
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", 2))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetUserByUsername() throws Exception {
		//Testing the get user by username call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?username={username}", "test_username"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user.getBio()));

		//Testing the get user by username call is not found
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?username={username}", "not_a_user"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testCreateUser() throws Exception {
		User newUser = Mockito.spy(new User("test_username3", "test bio 3"));
		Mockito.when(newUser.getId()).thenReturn(3);
		UserRequestDTO newUserDTO = UserRequestDTO.toDTO(newUser);
		Mockito.when(userService.createUser(UserCreateRequestDTO.toDTO(newUser))).thenReturn(newUserDTO);

		UserCreateRequestDTO requestDTO = UserCreateRequestDTO.toDTO(newUser);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the create user call is OK
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		//Testing the create user call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the create user call errors when illegal arguments passed
		Mockito.doThrow(IllegalArgumentException.class).when(userService).createUser(any(UserCreateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the create user call errors when username is taken
		Mockito.doThrow(DataIntegrityViolationException.class).when(userService).createUser(any(UserCreateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

	//TODO test auth once implemented
	@Test
	public void testUpdateUser() throws Exception {
		UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO(1, "test_username2", "test bio 2");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the update user call is accepted
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the update user call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		// Testing the update user call errors when user is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).updateUser(any(UserUpdateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		// Testing the update user call errors when illegal arguments passed
		Mockito.doThrow(IllegalArgumentException.class).when(userService).updateUser(any(UserUpdateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the update user call errors when username is taken
		Mockito.doThrow(DataIntegrityViolationException.class).when(userService).updateUser(any(UserUpdateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

	// TODO test auth once implemented
	@Test
	public void testDeleteUser() throws Exception {
		//Testing the delete user call is OK
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", user.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk());

		//Testing the delete user call is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).deleteUser(3);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", 3))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
