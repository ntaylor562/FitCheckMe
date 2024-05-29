package com.fitcheckme.FitCheckMe.unit_tests.controllers;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
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

		CustomUserDetails userDetails = new CustomUserDetails(user1.getId(), user1.getUsername(), "", user1.getRoles());
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
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/all"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	public void testGetUserById() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?id={id}", user1.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user1.getBio()));
	}

	@Test
	public void testGetUserAndExpectEntityNotFoundException() throws Exception {
		Mockito.when(userService.getById(3)).thenThrow(EntityNotFoundException.class);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?id={id}", 3))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetUserByUsername() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?username={username}", user1.getUsername()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user1.getBio()));
	}

	@Test
	public void testGetUserByUsernameAndExpectEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user?username={username}", "not_a_user"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testGetCurrentUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/currentuser"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.bio").value(user1.getBio()));
	}

	@Test
	public void testGetCurrentUserAndExpectEntityNotFoundException() throws Exception {
		Mockito.when(userService.getById(user1.getId())).thenThrow(EntityNotFoundException.class);
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

		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}

	@Test
	public void testCreateUserWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateUserAndExpectIllegalArgumentsException() throws Exception {
		User newUser = Mockito.spy(new User("test_username3", "test3@email.com", "pass3", "test bio 3", Set.of()));
		Mockito.when(newUser.getId()).thenReturn(3);
		UserRequestDTO newUserDTO = UserRequestDTO.toDTO(newUser);
		Mockito.when(userService.createUser(UserCreateRequestDTO.toDTO(newUser))).thenReturn(newUserDTO);

		UserCreateRequestDTO requestDTO = UserCreateRequestDTO.toDTO(newUser);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);

		Mockito.doThrow(IllegalArgumentException.class).when(userService).createUser(any(UserCreateRequestDTO.class));
		mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testCreateUserWithConflictingUsername() throws Exception {
		User newUser = Mockito.spy(new User("test_username3", "test3@email.com", "pass3", "test bio 3", Set.of()));
		Mockito.when(newUser.getId()).thenReturn(3);
		UserRequestDTO newUserDTO = UserRequestDTO.toDTO(newUser);
		Mockito.when(userService.createUser(UserCreateRequestDTO.toDTO(newUser))).thenReturn(newUserDTO);

		UserCreateRequestDTO requestDTO = UserCreateRequestDTO.toDTO(newUser);
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
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
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());
	}

	@Test
	public void testUpdateUserDetailsWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdateUserDetailsAndExpectEntityNotFoundException() throws Exception {
		UserUpdateDetailsRequestDTO requestDTO = new UserUpdateDetailsRequestDTO(1, "test_username2", "test bio 2");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(EntityNotFoundException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateUserDetailsAndExpectIllegalArgumentsException() throws Exception {
		UserUpdateDetailsRequestDTO requestDTO = new UserUpdateDetailsRequestDTO(1, "test_username2", "test bio 2");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(IllegalArgumentException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdateUserDetailsWithConflictingUsername() throws Exception {
		UserUpdateDetailsRequestDTO requestDTO = new UserUpdateDetailsRequestDTO(1, "test_username2", "test bio 2");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(DataIntegrityViolationException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isConflict());
	}

	@Test
	public void testUpdateUserDetailsAndExpectAccessDeniedException() throws Exception {
		UserUpdateDetailsRequestDTO requestDTO = new UserUpdateDetailsRequestDTO(1, "test_username2", "test bio 2");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(AccessDeniedException.class).when(userService).updateUserDetails(any(UserUpdateDetailsRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/details")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testAddUserRole() throws Exception {
		UserRoleUpdateDTO requestDTO = new UserRoleUpdateDTO(1, "SUPER_ADMIN");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/addrole")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());
	}

	@Test
	public void testAddUserRoleWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/addrole"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testAddUserRoleAndExpectEntityNotFoundException() throws Exception {
		UserRoleUpdateDTO requestDTO = new UserRoleUpdateDTO(1, "SUPER_ADMIN");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
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
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/removerole")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());
	}

	@Test
	public void testRemoveUserRoleWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/removerole"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testRemoveUserRoleAndExpectEntityNotFoundException() throws Exception {
		UserRoleUpdateDTO requestDTO = new UserRoleUpdateDTO(1, "SUPER_ADMIN");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
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
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isAccepted());
	}

	@Test
	public void testUpdateUserPasswordWithNoBody() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdateUserPasswordAndExpectEntityNotFoundException() throws Exception {
		UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(1, "passw", "pass3");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(EntityNotFoundException.class).when(userService).updatePassword(any(UserUpdatePasswordRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testUpdateUserPasswordAndExpectIllegalArgumentsException() throws Exception {
		UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(1, "passw", "pass3");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(IllegalArgumentException.class).when(userService).updatePassword(any(UserUpdatePasswordRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdateUserPasswordAndExpectAccessDeniedException() throws Exception {
		UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(1, "passw", "pass3");
		String requestBody = new ObjectMapper().writeValueAsString(requestDTO);
		Mockito.doThrow(AccessDeniedException.class).when(userService).updatePassword(any(UserUpdatePasswordRequestDTO.class), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/user/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	public void testDeleteUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user?id={id}", user1.getId()))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testDeleteUserAndExpectEntityNotFoundException() throws Exception {
		Mockito.doThrow(EntityNotFoundException.class).when(userService).deleteUser(anyInt(), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user?id={id}", 3))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void testDeleteUserAndExpectAccessDeniedException() throws Exception {
		Mockito.doThrow(AccessDeniedException.class).when(userService).deleteUser(anyInt(), any(CustomUserDetails.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/user?id={id}", user1.getId()))
			.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

}
