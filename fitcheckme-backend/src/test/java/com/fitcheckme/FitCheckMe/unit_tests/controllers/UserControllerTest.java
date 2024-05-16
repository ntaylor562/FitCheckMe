package com.fitcheckme.FitCheckMe.unit_tests.controllers;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.controllers.UserController;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	private User user1;
	private User user2;

	@BeforeEach
	public void setUp() {
		this.user1 = Mockito.spy(new User("test_username1", "test1@email.com", "pass1", "test bio1", Set.of()));
		this.user2 = Mockito.spy(new User("test_username2", "test2@email.com", "pass2", "test bio2", Set.of()));
		Mockito.when(this.user1.getId()).thenReturn(1);
		Mockito.when(this.user2.getId()).thenReturn(2);

		UserRequestDTO userDTO1 = UserRequestDTO.toDTO(this.user1);
		Mockito.when(userService.getById(1)).thenReturn(userDTO1);
		UserRequestDTO userDTO2 = UserRequestDTO.toDTO(this.user2);
		Mockito.when(userService.getById(2)).thenReturn(userDTO2);

		Mockito.when(userService.getById(3)).thenThrow(EntityNotFoundException.class);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(user1.getUsername())
			.password("")
			.build();
		Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        // Set up the SecurityContextHolder with the mock Authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

		Mockito.when(userService.getByUsername("test_username1")).thenReturn(userDTO1);
		Mockito.when(userService.getByUsername("test_username2")).thenReturn(userDTO2);
		Mockito.when(userService.getByUsername("not_a_user")).thenThrow(EntityNotFoundException.class);
	}

	@Test
	public void testGetAllUsers() throws Exception {
		List<UserRequestDTO> userList = List.of(UserRequestDTO.toDTO(user1), UserRequestDTO.toDTO(user2));
		Mockito.when(userService.getAll()).thenReturn(userList);
		//Testing the get all users call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/all"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetUser() throws Exception {
		//Testing the get user by id call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?id={id}", user1.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user1.getBio()));
			
		//Testing the get user by id call is not found
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?id={id}", 3))
			.andExpect(MockMvcResultMatchers.status().isNotFound());

		//Testing the get user by username call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?username={username}", user1.getUsername()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user1.getBio()));

		//Testing the get user by username call is not found
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?username={username}", "not_a_user"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetCurrentUser() throws Exception {
		//Testing the get current user call is OK
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/currentuser"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user1.getBio()));
		
		//Testing the get current user call is not found
		Mockito.when(userService.getByUsername(user1.getUsername())).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/currentuser"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testCreateUser() throws Exception {
		User newUser = Mockito.spy(new User("test_username3", "test3@email.com", "pass3", "test bio 3", Set.of()));
		Mockito.when(newUser.getId()).thenReturn(3);
		UserRequestDTO newUserDTO = UserRequestDTO.toDTO(newUser);
		Mockito.when(userService.createUser(UserCreateRequestDTO.toDTO(newUser))).thenReturn(newUserDTO);

		UserCreateRequestDTO requestDTO = UserCreateRequestDTO.toDTO(newUser);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the create user call is OK
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isCreated());

		//Testing the create user call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the create user call errors when illegal arguments passed
		Mockito.doThrow(IllegalArgumentException.class).when(userService).createUser(any(UserCreateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the create user call errors when username is taken
		Mockito.doThrow(DataIntegrityViolationException.class).when(userService).createUser(any(UserCreateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

	@Test
	public void testUpdateUserDetails() throws Exception {
		UserUpdateDetailsRequestDTO requestDTO = new UserUpdateDetailsRequestDTO(1, "test_username2", "test bio 2");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the update user call is accepted
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the update user call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
		// Testing the update user call errors when user is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		// Testing the update user call errors when illegal arguments passed
		Mockito.doThrow(IllegalArgumentException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the update user call errors when username is taken
		Mockito.doThrow(DataIntegrityViolationException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isConflict());

		// Testing the update user call errors when user does not have permission
		Mockito.doThrow(AccessDeniedException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testAddUserRole() throws Exception {
		UserRoleUpdateDTO requestDTO = new UserRoleUpdateDTO(1, "SUPER_ADMIN");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the add user role call is accepted
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/addrole")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the add user role call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/addrole"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the add user role call errors when user or role is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).addUserRole(any(UserRoleUpdateDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/addrole")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testRemoveUserRole() throws Exception {
		UserRoleUpdateDTO requestDTO = new UserRoleUpdateDTO(1, "SUPER_ADMIN");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the remove user role call is accepted
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/removerole")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the remove user role call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/removerole"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the remove user role call errors when user or role is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).removeUserRole(any(UserRoleUpdateDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/removerole")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(1, "passw", "pass3");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		//Testing the update user password call is accepted
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());

		//Testing the update user password call errors when it is given no body
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the update user password call errors when user is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).updatePassword(any(UserUpdatePasswordRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

		// Testing the update user password call errors when illegal arguments passed
		Mockito.doThrow(IllegalArgumentException.class).when(userService).updatePassword(any(UserUpdatePasswordRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		// Testing the update user password call errors when user does not have permission
		Mockito.doThrow(AccessDeniedException.class).when(userService).updatePassword(any(UserUpdatePasswordRequestDTO.class), any(UserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	// TODO test auth once implemented
	@Test
	public void testDeleteUser() throws Exception {
		//Testing the delete user call is OK
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user?id={id}", user1.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk());

		//Testing the delete user call is not found
		Mockito.doThrow(EntityNotFoundException.class).when(userService).deleteUser(3);
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user?id={id}", 3))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
