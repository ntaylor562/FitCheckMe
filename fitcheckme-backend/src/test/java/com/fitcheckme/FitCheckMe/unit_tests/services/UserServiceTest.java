package com.fitcheckme.FitCheckMe.unit_tests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.RefreshTokenRepository;
import com.fitcheckme.FitCheckMe.repositories.RoleRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.services.UserService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private GarmentRepository garmentRepository;

	@Mock
	private OutfitRepository outfitRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	private final Integer maxUsernameLength = 50;
	private final Integer maxEmailLength = 100;
	private final Integer maxBioLength = 300;

	@BeforeEach
	public void setup() {

	}

	@Test
	public void testGetAllAndExpectListOfUsers() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		User user2 = Mockito.spy(new User("user2", "user2@test.com", "password2", null, null));
		Mockito.when(user1.getId()).thenReturn(1);
		Mockito.when(user2.getId()).thenReturn(2);

		Mockito.when(userRepository.findAllByOrderByIdAsc()).thenReturn(List.of(user1, user2));
		List<UserRequestDTO> result = userService.getAll();

		assertThat(result).hasSize(2)
				.allMatch(user -> user.getClass().equals(UserRequestDTO.class));
	}

	@Test
	public void testGetAllAndExpectEmptyList() {
		Mockito.when(userRepository.findAllByOrderByIdAsc()).thenReturn(List.of());
		List<UserRequestDTO> result = userService.getAll();
		assertThat(result).isEmpty();
	}

	@Test
	public void testGetByIdAndExpectUser() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		UserRequestDTO result = userService.getById(1);
		assertThat(result).isNotNull()
				.isEqualTo(UserRequestDTO.toDTO(user1));
	}

	@Test
	public void testGetByIdAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.findById(2)).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> userService.getById(2));
	}

	@Test
	public void testGetByUsernameAndExpectUser() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(Optional.of(user1));
		UserRequestDTO result = userService.getByUsername("user1");
		assertThat(result).isNotNull()
				.isEqualTo(UserRequestDTO.toDTO(user1));
	}

	@Test
	public void testGetByUsernameAndExpectEntityNotFoundException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		Mockito.when(userRepository.findByUsernameIgnoreCase("user2")).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> userService.getByUsername("user2"));
	}

	@Test
	public void testExistsAndExpectTrue() {
		Mockito.when(userRepository.existsById(1)).thenReturn(true);
		assertThat(userService.exists(1)).isTrue();
	}

	@Test
	public void testExistsAndExpectFalse() {
		Mockito.when(userRepository.existsById(2)).thenReturn(false);
		assertThat(userService.exists(2)).isFalse();
	}

	@Test
	public void testCreateUserAndExpectUser() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxEmailLength", this.maxEmailLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(userRepository.existsByUsernameIgnoreCase("user1")).thenReturn(false);
		Mockito.when(userRepository.existsByEmailIgnoreCase(user1.getEmail())).thenReturn(false);
		Mockito.when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(new Role("USER")));

		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);
		UserRequestDTO result = userService.createUser(UserCreateRequestDTO.toDTO(user1));
		assertThat(result).isNotNull()
				.isEqualTo(UserRequestDTO.toDTO(user1));
	}

	@Test
	public void givenUsernameAlreadyExists_whenCreateUser_thenThrowDataIntegrityViolation() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxEmailLength", this.maxEmailLength);

		Mockito.when(userRepository.existsByUsernameIgnoreCase("user1")).thenReturn(true);
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(
						() -> userService.createUser(new UserCreateRequestDTO("user1", "user1@test.com", "password1")));
	}

	@Test
	public void givenEmailAlreadyExists_whenCreateUser_thenThrowDataIntegrityViolation() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxEmailLength", this.maxEmailLength);

		Mockito.when(userRepository.existsByEmailIgnoreCase("user1@test.com")).thenReturn(true);
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(
						() -> userService.createUser(new UserCreateRequestDTO("user1", "user1@test.com", "password1")));
	}

	@Test
	public void givenUsernameTooLong_whenCreateUser_thenThrowIllegalArgumentException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxEmailLength", this.maxEmailLength);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService
						.createUser(new UserCreateRequestDTO("user1".repeat(100), "user1@test.com", "password1")));
	}

	@Test
	public void givenInvalidUsername_whenCreateUser_thenThrowIllegalArgumentException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxEmailLength", this.maxEmailLength);

		String invalidUsername = "#";

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService
						.createUser(new UserCreateRequestDTO(invalidUsername, "user1@test.com", "password1")));
	}

	@Test
	public void givenEmailTooLong_whenCreateUser_thenThrowIllegalArgumentException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxEmailLength", this.maxEmailLength);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService
						.createUser(new UserCreateRequestDTO("user1", "user1@test.com".repeat(100), "password1")));
	}

	@Test
	public void testUpdateUsernameAndExpectUser() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.existsByUsernameIgnoreCase("user2")).thenReturn(false);
		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

		UserRequestDTO result = userService.updateUserDetails(new UserUpdateDetailsRequestDTO(1, "user2", null),
				userDetails);
		assertThat(result).isNotNull();
		assertThat(result.username()).isEqualTo("user2");
		assertThat(result.bio()).isEqualTo(user1.getBio());
	}

	@Test
	public void givenUsernameAlreadyExists_whenUpdateUsername_thenThrowDataIntegrityViolation() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.existsByUsernameIgnoreCase("user2")).thenReturn(true);

		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> userService.updateUserDetails(new UserUpdateDetailsRequestDTO(1, "user2", null),
						userDetails));
	}

	@Test
	public void givenUsernameTooLong_whenUpdateUsername_thenThrowIllegalArgumentException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService
						.updateUserDetails(new UserUpdateDetailsRequestDTO(1, "user2".repeat(100), null), userDetails));
	}

	@Test
	public void givenInvalidUsername_whenUpdateUsername_thenThrowIllegalArgumentException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		String invalidUsername = "#";

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService
						.updateUserDetails(new UserUpdateDetailsRequestDTO(1, invalidUsername, null), userDetails));
	}

	@Test
	public void testUpdateBioAndExpectUser() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

		UserRequestDTO result = userService.updateUserDetails(new UserUpdateDetailsRequestDTO(1, null, "new bio"),
				userDetails);
		assertThat(result).isNotNull();
		assertThat(result.username()).isEqualTo("user1");
		assertThat(result.bio()).isEqualTo("new bio");
	}

	@Test
	public void givenBioTooLong_whenUpdateBio_thenThrowIllegalArgumentException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService
						.updateUserDetails(new UserUpdateDetailsRequestDTO(1, null, "new bio".repeat(100)),
								userDetails));
	}

	@Test
	public void testUpdateUserDetailsAndExpectUser() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(userRepository.existsByUsernameIgnoreCase("user2")).thenReturn(false);
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

		UserRequestDTO result = userService.updateUserDetails(new UserUpdateDetailsRequestDTO(1, "user2", "new bio"),
				userDetails);
		assertThat(result).isNotNull();
		assertThat(result.username()).isEqualTo("user2");
		assertThat(result.bio()).isEqualTo("new bio");
	}

	@Test
	public void testUpdateUserDetailsAndExpectAccessDeniedException() {
		ReflectionTestUtils.setField(userService, "maxUsernameLength", this.maxUsernameLength);
		ReflectionTestUtils.setField(userService, "maxBioLength", this.maxBioLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user3")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(userRepository.existsByUsernameIgnoreCase("user2")).thenReturn(false);

		assertThatExceptionOfType(AccessDeniedException.class)
				.isThrownBy(() -> userService.updateUserDetails(new UserUpdateDetailsRequestDTO(1, "user2", "new bio"),
						userDetails));
	}

	@Test
	public void testUpdatePassword() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(passwordEncoder.encode("password2")).thenReturn("password2Encoded");
		Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

		assertThatNoException().isThrownBy(() -> userService
				.updatePassword(new UserUpdatePasswordRequestDTO(1, "password1", "password2"), userDetails));
	}

	@Test
	public void testUpdatePasswordAndExpectIllegalArgumentException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService
				.updatePassword(new UserUpdatePasswordRequestDTO(1, "password1", "password2"), userDetails));
	}

	@Test
	public void testUpdatePasswordAndExpectAccessDeniedException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user2")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> userService
				.updatePassword(new UserUpdatePasswordRequestDTO(1, "password1", "password2"), userDetails));
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testAddUserRole() {
		User user1 = Mockito.spy(new User("user1", "user@test.com", "password1", null, new HashSet<>()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(roleRepository.findByRoleNameIgnoreCase("USER_ADMIN"))
				.thenReturn(Optional.of(new Role("USER_ADMIN")));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

		assertThatNoException().isThrownBy(() -> userService.addUserRole(new UserRoleUpdateDTO(1, "USER_ADMIN")));
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testAddUserRoleAndExpectEntityNotFoundException() {
		User user1 = Mockito.spy(new User("user1", "user@test.com", "password1", null, Set.of()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(roleRepository.findByRoleNameIgnoreCase("USER_ADMIN")).thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> userService.addUserRole(new UserRoleUpdateDTO(1, "USER_ADMIN")));
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testRemoveUserRole() {
		User user1 = Mockito.spy(new User("user1", "user@test.com", "password1", null, new HashSet<>()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(roleRepository.findByRoleNameIgnoreCase("USER_ADMIN"))
				.thenReturn(Optional.of(new Role("USER_ADMIN")));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user1);

		assertThatNoException().isThrownBy(() -> userService.removeUserRole(new UserRoleUpdateDTO(1, "USER_ADMIN")));
	}

	@Test
	@WithMockUser(roles = "SUPER_ADMIN")
	public void testRemoveUserRoleAndExpectEntityNotFoundException() {
		User user1 = Mockito.spy(new User("user1", "user@test.com", "password1", null, new HashSet<>()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(roleRepository.findByRoleNameIgnoreCase("USER_ADMIN")).thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> userService.removeUserRole(new UserRoleUpdateDTO(1, "USER_ADMIN")));
	}

	@Test
	public void testDeleteUser() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);
		Garment garment1 = Mockito.spy(new Garment("garment 1", user1, List.of(), List.of()));
		Mockito.when(garment1.getId()).thenReturn(1);
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(), List.of()));
		Mockito.when(outfit1.getId()).thenReturn(1);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(garmentRepository.findByUserId(1)).thenReturn(List.of(garment1));
		Mockito.when(outfitRepository.findByUserId(1)).thenReturn(List.of(outfit1));

		assertThatNoException().isThrownBy(() -> userService.deleteUser(1, userDetails));

		Mockito.verify(garmentRepository, Mockito.times(1)).deleteAllGarmentURLsByGarmentIds(List.of(garment1.getId()));
		Mockito.verify(garmentRepository, Mockito.times(1)).deleteAllGarmentTagsByGarmentIds(List.of(garment1.getId()));
		Mockito.verify(garmentRepository, Mockito.times(1)).deleteAllGarmentsFromOutfits(List.of(garment1.getId()));
		Mockito.verify(garmentRepository, Mockito.times(1)).deleteAllByUserId(user1.getId());

		Mockito.verify(outfitRepository, Mockito.times(1)).deleteAllOutfitsFromGarments(List.of(outfit1.getId()));
		Mockito.verify(outfitRepository, Mockito.times(1)).deleteAllOutfitTagsByOutfitIds(List.of(outfit1.getId()));
		Mockito.verify(outfitRepository, Mockito.times(1)).deleteAllByUserId(user1.getId());

		Mockito.verify(userRepository, Mockito.times(1)).deleteById(user1.getId());
	}

	@Test
	public void testDeleteUserAndExpectEntityNotFoundException() {
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findById(2)).thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> userService.deleteUser(2, userDetails));
	}

	// TODO test follow user and delete user
}
