package com.fitcheckme.FitCheckMe.unit_tests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.services.GarmentService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GarmentServiceTest {

	@InjectMocks
	private GarmentService garmentService;

	@Mock
	GarmentRepository garmentRepository;

	@Mock
	TagRepository tagRepository;

	@Mock
	UserRepository userRepository;

	private final Integer maxGarmentNameLength = 50;
	private final Integer maxURLsPerGarment = 5;
	private final Integer maxGarmentURLLength = 200;
	private final Integer maxTagsPerGarment = 20;

	@BeforeEach
	public void setup() {

	}

	private CustomUserDetails getUserDetails(Integer userId, String username, String password, Set<Role> authorities) {
		return new CustomUserDetails(userId, username, password, authorities);
	}

	@Test
	public void testGetAllAndExpectListOfGarments() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Mockito.when(tag1.getId()).thenReturn(1);
		Garment garment1 = new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1));
		Garment garment2 = new Garment(user1, "garment 2", List.of("url3", "url4"), List.of(tag1));

		Mockito.when(garmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of(garment1, garment2));
		List<GarmentRequestDTO> result = garmentService.getAll();

		assertThat(result).containsExactly(GarmentRequestDTO.toDTO(garment1), GarmentRequestDTO.toDTO(garment2));

		Mockito.verify(garmentRepository, Mockito.times(1)).findAllByOrderByIdAsc();
	}

	@Test
	public void testGetAllAndExpectEmptyList() {
		Mockito.when(garmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of());
		List<GarmentRequestDTO> result = garmentService.getAll();
		assertThat(result).isEmpty();

		Mockito.verify(garmentRepository, Mockito.times(1)).findAllByOrderByIdAsc();
	}

	@Test
	public void testGetByIdAndExpectGarment() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of()));
		Mockito.when(garment1.getId()).thenReturn(1);

		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.of(garment1));

		GarmentRequestDTO result = garmentService.getById(1);
		assertThat(result).isNotNull()
				.isEqualTo(GarmentRequestDTO.toDTO(garment1));

		Mockito.verify(garmentRepository, Mockito.times(1)).findById(any());
	}

	@Test
	public void testGetByIdAndExpectEntityNotFoundException() {
		Mockito.when(garmentRepository.findById(2)).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getById(2))
				.withMessage("Garment not found with ID: 2");

		Mockito.verify(garmentRepository, Mockito.times(1)).findById(any());
	}

	@Test
	public void givenListOfIds_whenGetById_thenReturnGarments() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of()));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of(), List.of()));
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);

		Mockito.when(garmentRepository.findAllById(List.of(1, 2))).thenReturn(List.of(garment1, garment2));

		List<GarmentRequestDTO> result = garmentService.getById(List.of(1, 2));
		assertThat(result).hasSize(2)
				.allMatch(garment -> garment.getClass().equals(GarmentRequestDTO.class));

		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
	}

	@Test
	public void givenListOfNonExistingIds_whenGetById_thenExpectEntityNotFoundException() {
		Mockito.when(garmentRepository.findAllById(List.of(1, 2))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getById(List.of(1, 2)))
				.withMessage("Garments with the following IDs not found: [1, 2]");

		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
	}

	@Test
	public void testGarmentExistsAndExpectTrue() {
		Mockito.when(garmentRepository.existsById(1)).thenReturn(true);
		assertThat(garmentService.exists(1)).isTrue();
		Mockito.verify(garmentRepository, Mockito.times(1)).existsById(any());
	}

	@Test
	public void testGarmentExistsAndExpectFalse() {
		Mockito.when(garmentRepository.existsById(1)).thenReturn(false);
		assertThat(garmentService.exists(1)).isFalse();
		Mockito.verify(garmentRepository, Mockito.times(1)).existsById(any());
	}

	@Test
	public void testGetUserGarmentsByUserIdAndExpectListOfGarments() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of()));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of(), List.of()));
		Mockito.when(user1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);

		Mockito.when(userRepository.existsById(1)).thenReturn(true);
		Mockito.when(garmentRepository.findByUserIdOrderByIdAsc(1)).thenReturn(List.of(garment1, garment2));

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getId());
		assertThat(result).hasSize(2)
				.allMatch(garment -> garment.getClass().equals(GarmentRequestDTO.class));

		Mockito.verify(userRepository, Mockito.times(1)).existsById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findByUserIdOrderByIdAsc(any());
	}

	@Test
	public void testGetUserGarmentsByUserIdAndExpectEmptyList() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(userRepository.existsById(1)).thenReturn(true);
		Mockito.when(garmentRepository.findByUserIdOrderByIdAsc(1)).thenReturn(List.of());

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getId());
		assertThat(result).isEmpty();

		Mockito.verify(userRepository, Mockito.times(1)).existsById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findByUserIdOrderByIdAsc(any());
	}

	@Test
	public void testGetUserGarmentsByUserIdAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.existsById(1)).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getUserGarments(1))
				.withMessage("User not found with ID: 1");

		Mockito.verify(userRepository, Mockito.times(1)).existsById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findByUserIdOrderByIdAsc(any());
	}

	@Test
	public void testGetUserGarmentsByUsernameAndExpectListOfGarments() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of()));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of(), List.of()));
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);

		Mockito.when(userRepository.existsByUsernameIgnoreCase(user1.getUsername())).thenReturn(true);
		Mockito.when(garmentRepository.findByUser_UsernameIgnoreCaseOrderByIdAsc(user1.getUsername()))
				.thenReturn(List.of(garment1, garment2));

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getUsername());
		assertThat(result).hasSize(2)
				.allMatch(garment -> garment.getClass().equals(GarmentRequestDTO.class));

		Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findByUser_UsernameIgnoreCaseOrderByIdAsc(any());
	}

	@Test
	public void testGetUserGarmentsByUsernameAndExpectEmptyList() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		Mockito.when(userRepository.existsByUsernameIgnoreCase(user1.getUsername())).thenReturn(true);
		Mockito.when(garmentRepository.findByUser_UsernameIgnoreCaseOrderByIdAsc(user1.getUsername())).thenReturn(List.of());

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getUsername());
		assertThat(result).isEmpty();

		Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findByUser_UsernameIgnoreCaseOrderByIdAsc(any());
	}

	@Test
	public void testGetUserGarmentsByUsernameAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.existsByUsernameIgnoreCase("user1")).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getUserGarments("user1"))
				.withMessage("User not found with username: user1");

		Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(any());
		Mockito.verify(garmentRepository, Mockito.never()).findByUser_UsernameIgnoreCaseOrderByIdAsc(any());
	}

	@Test
	public void testCreateGarmentAndExpectGarment() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));
		Mockito.when(user1.getId()).thenReturn(1);
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);

		Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of(tag1));
		Mockito.when(garmentRepository.save(Mockito.any(Garment.class))).thenReturn(garment1);

		GarmentRequestDTO result = garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails);
		assertThat(result).isNotNull()
				.isEqualTo(GarmentRequestDTO.toDTO(garment1));

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).save(any());
	}

	@Test
	public void givenNonExistingUser_whenCreateGarment_thenExpectEntityNotFoundException() {
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService
						.createGarment(new GarmentCreateRequestDTO("garment 1", Set.of(), Set.of()), userDetails))
				.withMessage("User not found with ID: 1");

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingTag_whenCreateGarment_thenExpectEntityNotFoundException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of(tag1)));
		Mockito.when(user1.getId()).thenReturn(1);
		Mockito.when(tag1.getId()).thenReturn(1);

		Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails))
				.withMessage("Tags with the following IDs not found: [1]");

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenGarmentNameTooLong_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Garment garment1 = Mockito
				.spy(new Garment(user1, "a".repeat(this.maxGarmentNameLength + 1), List.of(), List.of()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails))
				.withMessage("Garment name too long, must be at most %d characters", this.maxGarmentNameLength);

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenTooManyGarmentURLs_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		List<String> urls = IntStream.range(0, this.maxURLsPerGarment + 1).mapToObj(n -> "url" + n).toList();
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", urls, List.of()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails))
				.withMessage("Too many URLs provided when creating a garment, must be at most %d URLs",
						this.maxURLsPerGarment);

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenGarmentURLTooLong_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Garment garment1 = Mockito
				.spy(new Garment(user1, "garment 1", List.of("a".repeat(this.maxGarmentURLLength + 1)), List.of()));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails))
				.withMessage("Garment URL too long, must be at most %d characters", this.maxGarmentURLLength);

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenTooManyGarmentTags_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Set<Tag> tags = IntStream.range(0, this.maxTagsPerGarment + 1).mapToObj(n -> {
			Tag tag = Mockito.spy(new Tag("tag" + n));
			Mockito.when(tag.getId()).thenReturn(n);
			return tag;
		}).collect(Collectors.toSet());
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), tags));

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails))
				.withMessage("Too many tags provided when creating a garment, must be at most %d tags",
						this.maxTagsPerGarment);

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).save(any());
	}

	@Test
	public void testCreateGarmentsAndExpectGarments() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of("url2"), List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of(tag1));
		Mockito.when(garmentRepository.save(any(Garment.class))).thenAnswer(i -> {
			Garment g = i.getArgument(0);
			if (g.getName().equals("garment 1"))
				return garment1;
			else if (g.getName().equals("garment 2"))
				return garment2;

			return g;
		});

		List<GarmentRequestDTO> result = garmentService.createGarment(
				List.of(GarmentCreateRequestDTO.toDTO(garment1), GarmentCreateRequestDTO.toDTO(garment2)), userDetails);
		assertThat(result).containsExactly(GarmentRequestDTO.toDTO(garment1), GarmentRequestDTO.toDTO(garment2));

		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(2)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(2)).save(any());
	}

	@Test
	public void testUpdateGarmentAndExpectGarment() {
		// TODO implement test after changing updateGarment to be like outfit's update
		// method where it allows updating multiple fields through one method
	}

	@Test
	public void testDeleteGarment() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.of(garment1));

		assertThatNoException().isThrownBy(() -> garmentService.deleteGarment(1, userDetails));

		Mockito.verify(garmentRepository, Mockito.times(1)).findById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).deleteGarmentFromOutfits(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).delete(any());
	}

	@Test
	public void testDeleteGarmentAndExpectEntityNotFoundException() {
		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.empty());

		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.deleteGarment(1, userDetails))
				.withMessage("Garment not found with ID: 1");

		Mockito.verify(garmentRepository, Mockito.times(1)).findById(any());
		Mockito.verify(garmentRepository, Mockito.never()).deleteGarmentFromOutfits(any());
		Mockito.verify(garmentRepository, Mockito.never()).delete(any());
	}

	@Test
	public void testDeleteGarmentAndExpectAccessDeniedException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		CustomUserDetails userDetails = getUserDetails(2, "user2", "", null);
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));

		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.of(garment1));

		assertThatExceptionOfType(AccessDeniedException.class)
				.isThrownBy(() -> garmentService.deleteGarment(1, userDetails))
				.withMessage("User does not have permission to delete this garment");

		Mockito.verify(garmentRepository, Mockito.times(1)).findById(any());
		Mockito.verify(garmentRepository, Mockito.never()).deleteGarmentFromOutfits(any());
		Mockito.verify(garmentRepository, Mockito.never()).delete(any());
	}

}
