package com.fitcheckme.FitCheckMe.integration_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GarmentRepository garmentRepository;

	@Autowired
	private OutfitRepository outfitRepository;

	@Autowired
	private TagRepository tagRepository;

	@BeforeEach
	public void setup() {
		resetAuth();
	}

	private User createTestUser() {
		ResponseEntity<Object> createUserResponse = postCall("/api/user/create",
				new UserCreateRequestDTO("new_test_user", "new_test_user@test.com", "password"));
		return userRepository.findById(getObjectFromResponse(createUserResponse, UserRequestDTO.class).userId()).get();
	}

	@Test
	public void testGetAllUsersAndExpectList() {
		logout();
		login("test_super_admin");

		ResponseEntity<Object> response = getCall("/api/user/all");
		List<UserRequestDTO> users = getListOfObjectsFromResponse(response, UserRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(users.size()).isGreaterThan(0);
	}

	@Test
	public void testGetAllUsersAsUserAndExpectUnauthorized() {
		ResponseEntity<Object> response = getCall("/api/user/all", true);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testGetUserByIdAndExpectUser() {
		User user1 = userRepository.findById(1).get();

		ResponseEntity<Object> response = getCall("/api/user?id=1");
		UserRequestDTO user = getObjectFromResponse(response, UserRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(user).isEqualTo(UserRequestDTO.toDTO(user1));
	}

	@Test
	public void testGetUserByIdAndExpectNotFound() {
		ResponseEntity<Object> response = getCall("/api/user?id=999", true);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testGetUserByUsernameAndExpectUser() {
		User user1 = userRepository.findByUsernameIgnoreCase("test_user").get();

		ResponseEntity<Object> response = getCall("/api/user?username=test_user");
		UserRequestDTO user = getObjectFromResponse(response, UserRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(user).isEqualTo(UserRequestDTO.toDTO(user1));
	}

	@Test
	public void testGetUserByUsernameAndExpectNotFound() {
		ResponseEntity<Object> response = getCall("/api/user?username=nonexistent_user", true);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testGetUserWithNoIdOrUsernameAndExpectBadRequest() {
		ResponseEntity<Object> response = getCall("/api/user", true);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testGetCurrentUserAndExpectCurrentUser() {
		User user1 = userRepository.findByUsernameIgnoreCase("test_user").get();
		User user2 = userRepository.findByUsernameIgnoreCase("test_super_admin").get();

		ResponseEntity<Object> response = getCall("/api/user/currentuser");
		UserRequestDTO user = getObjectFromResponse(response, UserRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(user).isEqualTo(UserRequestDTO.toDTO(user1));

		logout();
		login("test_super_admin");

		response = getCall("/api/user/currentuser");
		user = getObjectFromResponse(response, UserRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(user).isEqualTo(UserRequestDTO.toDTO(user2));
	}

	@Test
	public void testCreateUserAndExpectUser() {
		List<User> users = userRepository.findAll();
		List<String> usernames = users.stream().map(User::getUsername).toList();

		UserCreateRequestDTO userCreateRequestDTO = new UserCreateRequestDTO(
				"test_new_user",
				"test_new_user@test.com",
				"test_user_password");

		ResponseEntity<Object> response = postCall("/api/user/create", userCreateRequestDTO);
		UserRequestDTO responseDTO = getObjectFromResponse(response, UserRequestDTO.class);
		List<User> newUsers = userRepository.findAll().stream().filter(u -> !usernames.contains(u.getUsername()))
				.toList();
		User createdUser = userRepository.findByUsernameIgnoreCase("test_new_user").get();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(responseDTO.username()).isEqualTo(userCreateRequestDTO.username());
		assertThat(newUsers.get(0).getUsername()).isEqualTo(userCreateRequestDTO.username());

		userRepository.delete(createdUser);
	}

	@Test
	public void testCreateUserWithExistingUsernameAndExpectConflict() {
		List<User> users = userRepository.findAll();

		UserCreateRequestDTO userCreateRequestDTO = new UserCreateRequestDTO(
				"test_user",
				"test_other_email@test.com",
				"test_user_password");

		ResponseEntity<Object> response = postCall("/api/user/create", userCreateRequestDTO, true);

		List<User> newUsers = userRepository.findAll();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(users.size()).isEqualTo(newUsers.size());
	}

	@Test
	public void testCreateUserWithExistingEmailAndExpectConflict() {
		List<User> users = userRepository.findAll();

		UserCreateRequestDTO userCreateRequestDTO = new UserCreateRequestDTO(
				"test_user_other_name",
				"test_user@test.com",
				"test_user_password");

		ResponseEntity<Object> response = postCall("/api/user/create", userCreateRequestDTO, true);

		List<User> newUsers = userRepository.findAll();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(users.size()).isEqualTo(newUsers.size());
	}

	@Test
	public void testCreateUserWithBadUsernameAndExpectBadRequest() {
		List<User> users = userRepository.findAll();

		UserCreateRequestDTO userCreateRequestDTO = new UserCreateRequestDTO(
				"bad username",
				"bad@test.com",
				"test_user_password");

		ResponseEntity<Object> response = postCall("/api/user/create", userCreateRequestDTO, true);

		List<User> newUsers = userRepository.findAll();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(users.size()).isEqualTo(newUsers.size());
	}

	@Test
	public void testUpdateUserDetailsAndExpectUser() {
		User createdUser = createTestUser();

		logout();
		login("new_test_user", "password");

		ResponseEntity<Object> updateUserResponse = putCall("/api/user/details",
				new UserUpdateDetailsRequestDTO(createdUser.getId(), "new_test_user_updated", "new bio"));
		UserRequestDTO updatedUser = getObjectFromResponse(updateUserResponse, UserRequestDTO.class);

		User updatedUserEntity = userRepository.findById(createdUser.getId()).get();

		assertThat(updateUserResponse.getStatusCode().isError()).isFalse();
		assertThat(updatedUser.userId()).isEqualTo(createdUser.getId());
		assertThat(updatedUser.username()).isEqualTo("new_test_user_updated")
				.isEqualTo(updatedUserEntity.getUsername());
		assertThat(updatedUser.bio()).isEqualTo("new bio").isEqualTo(updatedUserEntity.getBio());

		logout();
		userRepository.delete(userRepository.findByUsernameIgnoreCase("new_test_user_updated").get());
	}

	@Test
	public void testUpdateUserDetailsWithExistingUsernameAndExpectBadRequest() {
		User createdUser = createTestUser();

		logout();
		login("new_test_user", "password");

		ResponseEntity<Object> updateUserResponse = putCall("/api/user/details",
				new UserUpdateDetailsRequestDTO(createdUser.getId(), "test_user", "new bio"), true);

		User newUserEntity = userRepository.findById(createdUser.getId()).get();

		assertThat(updateUserResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(newUserEntity.getId()).isEqualTo(createdUser.getId());
		assertThat(newUserEntity.getUsername()).isEqualTo("new_test_user");
		assertThat(newUserEntity.getBio()).isEqualTo(null);

		logout();
		userRepository.delete(newUserEntity);
	}

	@Test
	public void testAddAndRemoveUserRole() {
		logout();
		login("test_super_admin");

		User createdUser = createTestUser();

		ResponseEntity<Object> addRoleResponse = putCall("/api/user/addrole",
				new UserRoleUpdateDTO(createdUser.getId(), "SUPER_ADMIN"));

		List<Role> roles = userRepository.findRolesByUserId(createdUser.getId());

		assertThat(addRoleResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(roles.stream().anyMatch(r -> r.getName().equals("SUPER_ADMIN"))).isTrue();

		ResponseEntity<Object> removeRoleResponse = putCall("/api/user/removerole",
				new UserRoleUpdateDTO(createdUser.getId(), "SUPER_ADMIN"));

		roles = userRepository.findRolesByUserId(createdUser.getId());

		assertThat(removeRoleResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(roles.stream().anyMatch(r -> r.getName().equals("SUPER_ADMIN"))).isFalse();

		logout();
		userRepository.deleteById(createdUser.getId());
	}

	@Test
	public void testAddRoleAsUserAndExpectForbidden() {
		ResponseEntity<Object> addRoleResponse = putCall("/api/user/addrole",
				new UserRoleUpdateDTO(1, "SUPER_ADMIN"), true);

		assertThat(addRoleResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testRemoveRoleAsUserAndExpectForbidden() {
		ResponseEntity<Object> addRoleResponse = putCall("/api/user/removerole",
				new UserRoleUpdateDTO(1, "SUPER_ADMIN"), true);

		assertThat(addRoleResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testUpdateUserPasswordAndExpectAccepted() {
		User createdUser = createTestUser();

		logout();
		login("new_test_user", "password");

		String oldPassHash = userRepository.findById(createdUser.getId()).get().getPassword();

		ResponseEntity<Object> updatePasswordResponse = putCall("/api/user/password",
				new UserUpdatePasswordRequestDTO(createdUser.getId(), "password", "password2"));

		String newPassHash = userRepository.findById(createdUser.getId()).get().getPassword();

		logout();
		login("new_test_user", "password2");

		assertThat(updatePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		assertThat(newPassHash).isNotEqualTo(oldPassHash);

		logout();
		userRepository.delete(userRepository.findByUsernameIgnoreCase("new_test_user").get());
	}

	@Test
	public void testUpdateUserPasswordWithBadOldPasswordAndExpectBadRequest() {
		User createdUser = createTestUser();

		logout();
		login("new_test_user", "password");

		String oldPassHash = userRepository.findById(createdUser.getId()).get().getPassword();

		ResponseEntity<Object> updatePasswordResponse = putCall("/api/user/password",
				new UserUpdatePasswordRequestDTO(createdUser.getId(), "wrong_password", "password2"), true);

		String newPassHash = userRepository.findById(createdUser.getId()).get().getPassword();

		logout();
		login("new_test_user", "password");

		assertThat(updatePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(newPassHash).isEqualTo(oldPassHash);

		logout();
		userRepository.delete(userRepository.findByUsernameIgnoreCase("new_test_user").get());
	}

	@Test
	public void testUpdateUserPasswordAsDifferentUserAndExpectForbidden() {
		User user = userRepository.findByUsernameIgnoreCase("test_super_admin").get();
		String oldPassHash = user.getPassword();

		ResponseEntity<Object> updatePasswordResponse = putCall("/api/user/password",
				new UserUpdatePasswordRequestDTO(user.getId(), "test", "test2"), true);

		String newPassHash = userRepository.findByUsernameIgnoreCase("test_super_admin").get().getPassword();

		assertThat(updatePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(newPassHash).isEqualTo(oldPassHash);
	}

	@Test
	public void testDeleteUserAndExpectUserDeleted() {
		User createdUser = createTestUser();
		
		logout();
		login("new_test_user", "password");

		Tag tag1 = tagRepository.save(new Tag("tag 1"));
		Garment garment1 = garmentRepository.save(new Garment(createdUser, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Outfit outfit1 = outfitRepository.save(new Outfit(createdUser, "outfit 1", "outfit desc", LocalDateTime.now(), List.of(garment1), List.of(tag1)));

		ResponseEntity<Object> deleteUserResponse = deleteCall(String.format("/api/user?id=%d", createdUser.getId()));

		assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(userRepository.findById(createdUser.getId()).isEmpty()).isTrue();
		assertThat(garmentRepository.findById(garment1.getId()).isEmpty()).isTrue();
		assertThat(outfitRepository.findById(outfit1.getId()).isEmpty()).isTrue();
		assertThat(tagRepository.findById(tag1.getId()).isEmpty()).isFalse();

		tagRepository.delete(tag1);
	}

	@Test
	public void testDeleteUserAndExpectUserNotFound() {
		ResponseEntity<Object> deleteUserResponse = deleteCall("/api/user?id=999", true);

		assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeleteUserAsDifferentUserAndExpectForbidden() {
		User testUser = createTestUser();
		ResponseEntity<Object> deleteUserResponse = deleteCall(String.format("/api/user?id=%d", testUser.getId()), true);

		assertThat(deleteUserResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThat(userRepository.findById(testUser.getId()).isEmpty()).isFalse();

		userRepository.delete(testUser);
	}

}
