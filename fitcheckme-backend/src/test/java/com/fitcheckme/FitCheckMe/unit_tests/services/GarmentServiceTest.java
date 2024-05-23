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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
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

	@Test
	public void testGetAllAndExpectListOfGarments() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Mockito.when(tag1.getId()).thenReturn(1);
		Garment garment1 = new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1));
		Garment garment2 = new Garment(user1, "garment 2", List.of("url3", "url4"), List.of(tag1));

		Mockito.when(garmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of(garment1, garment2));
		List<GarmentRequestDTO> result = garmentService.getAll();

		assertThat(result).hasSize(2)
				.allMatch(garment -> garment.getClass().equals(GarmentRequestDTO.class));
	}

	@Test
	public void testGetAllAndExpectEmptyList() {
		Mockito.when(garmentRepository.findAllByOrderByIdAsc()).thenReturn(List.of());
		List<GarmentRequestDTO> result = garmentService.getAll();
		assertThat(result).isEmpty();
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
	}

	@Test
	public void testGetByIdAndExpectEntityNotFoundException() {
		Mockito.when(garmentRepository.findById(2)).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getById(2));
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
	}

	@Test
	public void givenListOfNonExistingIds_whenGetById_thenExpectEntityNotFoundException() {
		Mockito.when(garmentRepository.findAllById(List.of(1, 2))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getById(List.of(1, 2)));
	}

	@Test
	public void testGarmentExistsAndExpectTrue() {
		Mockito.when(garmentRepository.existsById(1)).thenReturn(true);
		assertThat(garmentService.exists(1)).isTrue();
	}

	@Test
	public void testGarmentExistsAndExpectFalse() {
		Mockito.when(garmentRepository.existsById(1)).thenReturn(false);
		assertThat(garmentService.exists(1)).isFalse();
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
		Mockito.when(garmentRepository.findByUserId(1)).thenReturn(List.of(garment1, garment2));

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getId());
		assertThat(result).hasSize(2)
				.allMatch(garment -> garment.getClass().equals(GarmentRequestDTO.class));
	}

	@Test
	public void testGetUserGarmentsByUserIdAndExpectEmptyList() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(userRepository.existsById(1)).thenReturn(true);
		Mockito.when(garmentRepository.findByUserId(1)).thenReturn(List.of());

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getId());
		assertThat(result).isEmpty();
	}

	@Test
	public void testGetUserGarmentsByUserIdAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.existsById(1)).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getUserGarments(1));
	}

	@Test
	public void testGetUserGarmentsByUsernameAndExpectListOfGarments() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of()));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of(), List.of()));
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);

		Mockito.when(userRepository.existsByUsernameIgnoreCase(user1.getUsername())).thenReturn(true);
		Mockito.when(garmentRepository.findByUser_UsernameIgnoreCase(user1.getUsername()))
				.thenReturn(List.of(garment1, garment2));

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getUsername());
		assertThat(result).hasSize(2)
				.allMatch(garment -> garment.getClass().equals(GarmentRequestDTO.class));
	}

	@Test
	public void testGetUserGarmentsByUsernameAndExpectEmptyList() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		Mockito.when(userRepository.existsByUsernameIgnoreCase(user1.getUsername())).thenReturn(true);
		Mockito.when(garmentRepository.findByUser_UsernameIgnoreCase(user1.getUsername())).thenReturn(List.of());

		List<GarmentRequestDTO> result = garmentService.getUserGarments(user1.getUsername());
		assertThat(result).isEmpty();
	}

	@Test
	public void testGetUserGarmentsByUsernameAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.existsByUsernameIgnoreCase("user1")).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.getUserGarments("user1"));
	}

	@Test
	public void testCreateGarmentAndExpectGarment() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));
		Mockito.when(tagRepository.findById(1)).thenReturn(Optional.of(tag1));
		Mockito.when(garmentRepository.save(Mockito.any(Garment.class))).thenReturn(garment1);

		GarmentRequestDTO result = garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails);
		assertThat(result).isNotNull()
				.isEqualTo(GarmentRequestDTO.toDTO(garment1));
	}

	@Test
	public void givenNonExistingUser_whenCreateGarment_thenExpectEntityNotFoundException() {
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();

		Mockito.when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService
						.createGarment(new GarmentCreateRequestDTO("garment 1", Set.of(), Set.of()), userDetails));
	}

	@Test
	public void givenNonExistingTag_whenCreateGarment_thenExpectEntityNotFoundException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));
		Mockito.when(tagRepository.findById(1)).thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails));
	}

	@Test
	public void givenGarmentNameTooLong_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Garment garment1 = Mockito.spy(new Garment(user1, "a".repeat(this.maxGarmentNameLength + 1), List.of(), List.of()));

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails));
	}

	@Test
	public void givenTooManyGarmentURLs_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		List<String> urls = IntStream.range(0, this.maxURLsPerGarment + 1).mapToObj(n -> "url" + n).toList();
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", urls, List.of()));

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails));
	}

	@Test
	public void givenGarmentURLTooLong_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("a".repeat(this.maxGarmentURLLength + 1)), List.of()));

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails));
	}

	@Test
	public void givenTooManyGarmentTags_whenCreateGarment_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Set<Tag> tags = IntStream.range(0, this.maxTagsPerGarment + 1).mapToObj(n -> {
			Tag tag = Mockito.spy(new Tag("tag" + n));
			Mockito.when(tag.getId()).thenReturn(n);
			return tag;
		}).collect(Collectors.toSet());
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of(), tags));

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> garmentService.createGarment(GarmentCreateRequestDTO.toDTO(garment1), userDetails));
	}

	@Test
	public void testCreateGarmentsAndExpectGarments() {
		ReflectionTestUtils.setField(garmentService, "maxGarmentNameLength", this.maxGarmentNameLength);
		ReflectionTestUtils.setField(garmentService, "maxURLsPerGarment", this.maxURLsPerGarment);
		ReflectionTestUtils.setField(garmentService, "maxGarmentURLLength", this.maxGarmentURLLength);
		ReflectionTestUtils.setField(garmentService, "maxTagsPerGarment", this.maxTagsPerGarment);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of("url2"), List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);

		Mockito.when(userRepository.findByUsernameIgnoreCase(user1.getUsername())).thenReturn(Optional.of(user1));
		Mockito.when(tagRepository.findById(1)).thenReturn(Optional.of(tag1));
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
	}

	@Test
	public void testUpdateGarmentAndExpectGarment() {
		// TODO implement test after changing updateGarment to be like outfit's update
		// method where it allows updating multiple fields through one method
	}

	@Test
	public void testDeleteGarment() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));

		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.of(garment1));

		assertThatNoException().isThrownBy(() -> garmentService.deleteGarment(1, userDetails));

		Mockito.verify(garmentRepository, Mockito.times(1)).deleteGarmentFromOutfits(1);
		Mockito.verify(garmentRepository, Mockito.times(1)).delete(garment1);
	}

	@Test
	public void testDeleteGarmentAndExpectEntityNotFoundException() {
		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.empty());

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user1")
				.password("")
				.build();
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> garmentService.deleteGarment(1, userDetails));

		Mockito.verify(garmentRepository, Mockito.never()).deleteGarmentFromOutfits(1);
	}

	@Test
	public void testDeleteGarmentAndExpectAccessDeniedException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username("user2")
				.password("")
				.build();
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1"), List.of(tag1)));

		Mockito.when(garmentRepository.findById(1)).thenReturn(Optional.of(garment1));

		assertThatExceptionOfType(AccessDeniedException.class)
				.isThrownBy(() -> garmentService.deleteGarment(1, userDetails));

		Mockito.verify(garmentRepository, Mockito.never()).deleteGarmentFromOutfits(1);
	}

}
