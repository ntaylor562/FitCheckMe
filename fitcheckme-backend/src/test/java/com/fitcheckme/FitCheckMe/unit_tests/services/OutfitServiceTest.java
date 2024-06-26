package com.fitcheckme.FitCheckMe.unit_tests.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;

import java.time.LocalDateTime;
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
import org.mockito.verification.VerificationMode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.services.OutfitService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class OutfitServiceTest {

	@InjectMocks
	private OutfitService outfitService;

	@Mock
	private OutfitRepository outfitRepository;

	@Mock
	private GarmentRepository garmentRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private UserRepository userRepository;

	private final Integer maxNameLength = 50;
	private final Integer maxDescLength = 300;
	private final Integer maxTagsPerOutfit = 20;
	private final Integer maxGarmentsPerOutfit = 20;

	@BeforeEach
	public void setup() {

	}

	private CustomUserDetails getUserDetails(Integer userId, String username, String password, Set<Role> authorities) {
		return new CustomUserDetails(userId, username, password, authorities);
	}

	private void verifyRepositoryCallsOnCreateOutfit(VerificationMode mode) {
		Mockito.verify(tagRepository, mode).findAllById(any());
		Mockito.verify(garmentRepository, mode).findAllById(any());
		Mockito.verify(userRepository, mode).findById(any());
		Mockito.verify(outfitRepository, mode).save(any());
	}

	@Test
	public void testGetAllAndExpectListOfOutfits() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Outfit outfit2 = Mockito
				.spy(new Outfit(user1, "outfit 2", "outfit 2 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(outfit2.getId()).thenReturn(2);

		Mockito.when(outfitRepository.findAllByOrderByIdAsc()).thenReturn(List.of(outfit1, outfit2));

		List<OutfitRequestDTO> outfits = outfitService.getAll();
		assertThat(outfits).hasSize(2)
				.allMatch(outfit -> outfit.getClass().equals(OutfitRequestDTO.class));

		Mockito.verify(outfitRepository, Mockito.times(1)).findAllByOrderByIdAsc();
	}

	@Test
	public void testGetAllAndExpectEmptyList() {
		Mockito.when(outfitRepository.findAllByOrderByIdAsc()).thenReturn(List.of());

		List<OutfitRequestDTO> outfits = outfitService.getAll();
		assertThat(outfits).isEmpty();

		Mockito.verify(outfitRepository, Mockito.times(1)).findAllByOrderByIdAsc();
	}

	@Test
	public void testGetByIdAndExpectOutfit() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(outfit1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(1)).thenReturn(java.util.Optional.of(outfit1));

		OutfitRequestDTO result = outfitService.getById(1);
		assertThat(result).isEqualTo(OutfitRequestDTO.toDTO(outfit1));

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
	}

	@Test
	public void testGetByIdAndExpectEntityNotFoundException() {
		Mockito.when(outfitRepository.findById(1)).thenReturn(java.util.Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> outfitService.getById(1))
				.withMessage("Outfit not found with ID: 1");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
	}

	@Test
	public void givenListOfIds_whenGetById_thenReturnOutfits() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Outfit outfit2 = Mockito
				.spy(new Outfit(user1, "outfit 2", "outfit 2 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(outfit2.getId()).thenReturn(2);

		Mockito.when(outfitRepository.findAllById(List.of(1, 2))).thenReturn(List.of(outfit1, outfit2));

		List<OutfitRequestDTO> outfits = outfitService.getById(List.of(1, 2));
		assertThat(outfits).hasSize(2)
				.allMatch(outfit -> outfit.getClass().equals(OutfitRequestDTO.class));

		Mockito.verify(outfitRepository, Mockito.times(1)).findAllById(any());
	}

	@Test
	public void givenListOfNonExistingIds_whenGetById_thenExpectEntityNotFoundException() {
		Mockito.when(outfitRepository.findAllById(List.of(1, 2))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> outfitService.getById(List.of(1, 2)))
				.withMessage("Outfits not found with IDs: [1, 2]");

		Mockito.verify(outfitRepository, Mockito.times(1)).findAllById(any());
	}

	@Test
	public void testOutfitExistsAndExpectTrue() {
		Mockito.when(outfitRepository.existsById(1)).thenReturn(true);

		assertThat(outfitService.exists(1)).isTrue();

		Mockito.verify(outfitRepository, Mockito.times(1)).existsById(any());
	}

	@Test
	public void testOutfitExistsAndExpectFalse() {
		Mockito.when(outfitRepository.existsById(1)).thenReturn(false);

		assertThat(outfitService.exists(1)).isFalse();

		Mockito.verify(outfitRepository, Mockito.times(1)).existsById(any());
	}

	@Test
	public void testGetUserOutfitsByUserIdAndExpectListOfOutfits() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Outfit outfit2 = Mockito
				.spy(new Outfit(user1, "outfit 2", "outfit 2 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(outfit2.getId()).thenReturn(2);

		Mockito.when(userRepository.existsById(user1.getId())).thenReturn(true);
		Mockito.when(outfitRepository.findByUserIdOrderByIdAsc(user1.getId())).thenReturn(List.of(outfit1, outfit2));

		List<OutfitRequestDTO> result = outfitService.getUserOutfits(user1.getId());
		assertThat(result).hasSize(2)
				.allMatch(outfit -> outfit.getClass().equals(OutfitRequestDTO.class));

		Mockito.verify(userRepository, Mockito.times(1)).existsById(any());
		Mockito.verify(outfitRepository, Mockito.times(1)).findByUserIdOrderByIdAsc(any());
	}

	@Test
	public void testGetUserOutfitsByUserIdAndExpectEmptyList() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(userRepository.existsById(1)).thenReturn(true);
		Mockito.when(outfitRepository.findByUserIdOrderByIdAsc(1)).thenReturn(List.of());

		List<OutfitRequestDTO> result = outfitService.getUserOutfits(user1.getId());
		assertThat(result).isEmpty();

		Mockito.verify(userRepository, Mockito.times(1)).existsById(any());
		Mockito.verify(outfitRepository, Mockito.times(1)).findByUserIdOrderByIdAsc(any());
	}

	@Test
	public void testGetUserOutfitsByUserIdAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.existsById(1)).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> outfitService.getUserOutfits(1))
				.withMessage("User not found with ID: 1");

		Mockito.verify(userRepository, Mockito.times(1)).existsById(any());
		Mockito.verify(outfitRepository, Mockito.never()).findByUserIdOrderByIdAsc(any());
	}

	@Test
	public void testGetUserOutfitsByUsernameAndExpectListOfOutfits() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Outfit outfit2 = Mockito
				.spy(new Outfit(user1, "outfit 2", "outfit 2 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(outfit2.getId()).thenReturn(2);

		Mockito.when(userRepository.existsByUsernameIgnoreCase(user1.getUsername())).thenReturn(true);
		Mockito.when(outfitRepository.findByUser_UsernameIgnoreCaseOrderByIdAsc(user1.getUsername()))
				.thenReturn(List.of(outfit1, outfit2));

		List<OutfitRequestDTO> result = outfitService.getUserOutfits(user1.getUsername());
		assertThat(result).hasSize(2)
				.allMatch(outfit -> outfit.getClass().equals(OutfitRequestDTO.class));

		Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(any());
		Mockito.verify(outfitRepository, Mockito.times(1)).findByUser_UsernameIgnoreCaseOrderByIdAsc(any());
	}

	@Test
	public void testGetUserOutfitsByUsernameAndExpectEmptyList() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));

		Mockito.when(userRepository.existsByUsernameIgnoreCase(user1.getUsername())).thenReturn(true);
		Mockito.when(outfitRepository.findByUser_UsernameIgnoreCaseOrderByIdAsc(user1.getUsername())).thenReturn(List.of());

		List<OutfitRequestDTO> result = outfitService.getUserOutfits(user1.getUsername());
		assertThat(result).isEmpty();

		Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(any());
		Mockito.verify(outfitRepository, Mockito.times(1)).findByUser_UsernameIgnoreCaseOrderByIdAsc(any());
	}

	@Test
	public void testGetUserOutfitsByUsernameAndExpectEntityNotFoundException() {
		Mockito.when(userRepository.existsByUsernameIgnoreCase("user1")).thenReturn(false);
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> outfitService.getUserOutfits("user1"))
				.withMessage("User not found with username: user1");

		Mockito.verify(userRepository, Mockito.times(1)).existsByUsernameIgnoreCase(any());
		Mockito.verify(outfitRepository, Mockito.never()).findByUser_UsernameIgnoreCaseOrderByIdAsc(any());
	}

	@Test
	public void testCreateOutfitAndExpectOutfit() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);
		ReflectionTestUtils.setField(outfitService, "maxTagsPerOutfit", this.maxTagsPerOutfit);
		ReflectionTestUtils.setField(outfitService, "maxGarmentsPerOutfit", this.maxGarmentsPerOutfit);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of("url3", "url4"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1, garment2),
						List.of(tag1)));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(user1.getId()).thenReturn(1);
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);
		Mockito.when(outfit1.getId()).thenReturn(1);

		Mockito.when(garmentRepository.findAllById(Set.of(1, 2))).thenReturn(List.of(garment1, garment2));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of(tag1));
		Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
		Mockito.when(outfitRepository.save(any(Outfit.class))).thenReturn(outfit1);

		OutfitRequestDTO result = outfitService.createOutfit(OutfitCreateRequestDTO.toDTO(outfit1), userDetails);
		assertThat(result).isEqualTo(OutfitRequestDTO.toDTO(outfit1));

		verifyRepositoryCallsOnCreateOutfit(Mockito.times(1));
	}

	@Test
	public void givenOutfitNameTooLong_whenCreateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("a".repeat(this.maxNameLength + 1),
								"desc", null, null), null))
				.withMessage("Outfit name must be at most %d characters", this.maxNameLength);

		verifyRepositoryCallsOnCreateOutfit(Mockito.never());
	}

	@Test
	public void givenOutfitDescTooLong_whenCreateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("outfit 1",
								"a".repeat(this.maxDescLength + 1), null, null), null))
				.withMessage("Outfit description must be at most %d characters", this.maxDescLength);

		verifyRepositoryCallsOnCreateOutfit(Mockito.never());
	}

	@Test
	public void givenTooManyOutfitTags_whenCreateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);
		ReflectionTestUtils.setField(outfitService, "maxTagsPerOutfit", this.maxTagsPerOutfit);

		List<Tag> tags = IntStream.range(0, this.maxTagsPerOutfit + 1).mapToObj(n -> {
			Tag tag = Mockito.spy(new Tag("tag" + n));
			Mockito.when(tag.getId()).thenReturn(n);
			return tag;
		}).toList();
		Set<Integer> tagIds = tags.stream().map(tag -> tag.getId()).collect(Collectors.toSet());

		Mockito.when(tagRepository.findAllById(tagIds)).thenReturn(tags);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("outfit 1",
								"desc", tagIds, null), null))
				.withMessage("Too many tags provided when creating outfit, must be at most %d tags",
						this.maxTagsPerOutfit);

		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(userRepository, Mockito.never()).findById(any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenTooManyGarments_whenCreateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);
		ReflectionTestUtils.setField(outfitService, "maxTagsPerOutfit", this.maxTagsPerOutfit);
		ReflectionTestUtils.setField(outfitService, "maxGarmentsPerOutfit", this.maxGarmentsPerOutfit);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));

		List<Garment> garments = IntStream.range(0, this.maxGarmentsPerOutfit + 1).mapToObj(n -> {
			Garment garment = Mockito.spy(new Garment(user1, "garment " + n, List.of(), List.of()));
			Mockito.when(garment.getId()).thenReturn(n);
			return garment;
		}).toList();
		Set<Integer> garmentIds = garments.stream().map(garment -> garment.getId()).collect(Collectors.toSet());

		Mockito.when(garmentRepository.findAllById(garmentIds)).thenReturn(garments);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("outfit 1",
								"desc", null, garmentIds), null))
				.withMessage("Too many garments provided when creating outfit, must be at most %d garments",
						this.maxGarmentsPerOutfit);

		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(userRepository, Mockito.never()).findById(any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingTag_whenCreateOutfit_thenExpectEntityNotFoundException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);

		Mockito.when(tagRepository.findAllById(anySet())).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("outfit 1",
								"a".repeat(this.maxNameLength + 1), Set.of(1, 2), null), null))
				.withMessageContaining("Tags not found with IDs: ");

		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(userRepository, Mockito.never()).findById(any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingGarment_whenCreateOutfit_thenExpectEntityNotFoundException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);

		Mockito.when(garmentRepository.findAllById(anySet())).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("outfit 1",
								"a".repeat(this.maxNameLength + 1), null, Set.of(1, 2)), null))
				.withMessageContaining("Garments not found with IDs: ");

		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(userRepository, Mockito.never()).findById(any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingUser_whenCreateOutfit_thenExpectEntityNotFoundException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);

		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);

		Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());
		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.createOutfit(new OutfitCreateRequestDTO("outfit 1",
								"a".repeat(this.maxNameLength + 1), null, null), userDetails))
				.withMessage("User not found with ID: 1");

		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(userRepository, Mockito.times(1)).findById(any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenUpdateNameDescAddGarmentRemoveGarmentAddTagRemoveTag_whenUpdateOutfit_thenExpectUpdatedOutfit() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);
		ReflectionTestUtils.setField(outfitService, "maxTagsPerOutfit", this.maxTagsPerOutfit);
		ReflectionTestUtils.setField(outfitService, "maxGarmentsPerOutfit", this.maxGarmentsPerOutfit);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Tag tag2 = Mockito.spy(new Tag("tag2"));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of(tag1)));
		Garment garment2 = Mockito.spy(new Garment(user1, "garment 2", List.of("url3", "url4"), List.of(tag1)));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), List.of(garment1),
						List.of(tag1)));
		Outfit updatedOutfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1 updated", "outfit 1 desc updated", LocalDateTime.now(),
						List.of(garment2),
						List.of(tag2)));

		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(user1.getId()).thenReturn(1);
		Mockito.when(tag1.getId()).thenReturn(1);
		Mockito.when(tag2.getId()).thenReturn(2);
		Mockito.when(garment1.getId()).thenReturn(1);
		Mockito.when(garment2.getId()).thenReturn(2);
		Mockito.when(outfit1.getId()).thenReturn(1);

		Mockito.when(garmentRepository.findAllById(Set.of(2))).thenReturn(List.of(garment2));
		Mockito.when(garmentRepository.findAllByOutfitIdAndIdsIn(outfit1.getId(), Set.of(1)))
				.thenReturn(List.of(garment1));
		Mockito.when(tagRepository.findAllByOutfitIdAndIdsIn(outfit1.getId(), Set.of(1))).thenReturn(List.of(tag1));
		Mockito.when(tagRepository.findAllById(Set.of(2))).thenReturn(List.of(tag2));
		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(outfitRepository.save(any(Outfit.class))).thenAnswer(i -> {
			Outfit outfit = i.getArgument(0);
			Mockito.when(outfit.getId()).thenReturn(1);
			return outfit;
		});

		OutfitRequestDTO result = outfitService.updateOutfit(
				new OutfitUpdateRequestDTO(outfit1.getId(), updatedOutfit1.getName(), updatedOutfit1.getDesc(),
						Set.of(garment2.getId()), Set.of(garment1.getId()), Set.of(tag2.getId()), Set.of(tag1.getId())),
				userDetails);
		assertThat(result.outfitName()).isEqualTo(updatedOutfit1.getName());

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.times(3)).save(any());
	}

	@Test
	public void givenNoUpdates_whenUpdateOutfit_thenExpectNoUpdate() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));

		assertThatNoException().isThrownBy(() -> outfitService.updateOutfit(
				new OutfitUpdateRequestDTO(1, null, null, null, null, null, null), userDetails));

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void testUpdateOutfitAndExpectAccessDeniedException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, null));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));

		assertThatExceptionOfType(AccessDeniedException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(outfit1.getId(), "test", null, null, null, null, null),
								userDetails))
				.withMessage("User does not have permission to edit this outfit");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenOutfitNameTooLong_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxNameLength", this.maxNameLength);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, "a".repeat(this.maxNameLength + 1), null, null, null,
										null,
										null),
								null))
				.withMessage("Outfit name must be at most %d characters", this.maxNameLength);

		Mockito.verify(outfitRepository, Mockito.never()).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenOutfitDescTooLong_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxDescLength", this.maxDescLength);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null,
										"a".repeat(this.maxDescLength + 1), null, null, null, null),
								null))
				.withMessage("Outfit description must be at most %d characters", this.maxDescLength);

		Mockito.verify(outfitRepository, Mockito.never()).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenTooManyGarments_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxGarmentsPerOutfit", this.maxGarmentsPerOutfit);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		List<Garment> garments = IntStream.range(0, this.maxGarmentsPerOutfit + 1).mapToObj(n -> {
			Garment garment = Mockito.spy(new Garment(user1, "garment " + n, List.of(), List.of()));
			Mockito.when(garment.getId()).thenReturn(n);
			return garment;
		}).toList();
		Set<Integer> garmentIds = garments.stream().map(garment -> garment.getId()).collect(Collectors.toSet());

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(garmentRepository.findAllById(garmentIds)).thenReturn(garments);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, garmentIds, null, null, null), userDetails))
				.withMessage("Too many garments provided when updating outfit, must be at most %d garments",
						this.maxGarmentsPerOutfit);

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenTooManyOutfitTags_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		ReflectionTestUtils.setField(outfitService, "maxTagsPerOutfit", this.maxTagsPerOutfit);

		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		List<Tag> tags = IntStream.range(0, this.maxTagsPerOutfit + 1).mapToObj(n -> {
			Tag tag = Mockito.spy(new Tag("tag" + n));
			Mockito.when(tag.getId()).thenReturn(n);
			return tag;
		}).toList();
		Set<Integer> tagIds = tags.stream().map(tag -> tag.getId()).collect(Collectors.toSet());

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(tagRepository.findAllById(tagIds)).thenReturn(tags);

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, null, null, tagIds, null), userDetails))
				.withMessage("Too many tags provided when updating outfit, must be at most %d tags",
						this.maxTagsPerOutfit);

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingAddGarment_whenUpdateOutfit_thenExpectEntityNotFoundException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(garmentRepository.findAllById(Set.of(1))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, Set.of(1), null, null, null), userDetails))
				.withMessage("One or more garments not found in add list");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingRemoveGarment_whenUpdateOutfit_thenExpectEntityNotFoundException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(garmentRepository.findAllByOutfitIdAndIdsIn(outfit1.getId(), Set.of(1))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, null, Set.of(1), null, null), userDetails))
				.withMessage("One or more garments not found in remove list");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingAddTag_whenUpdateOutfit_thenExpectEntityNotFoundException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, null, null, Set.of(1), null), userDetails))
				.withMessage("One or more tags not found in add list");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenNonExistingRemoveTag_whenUpdateOutfit_thenExpectEntityNotFoundException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(tagRepository.findAllByOutfitIdAndIdsIn(outfit1.getId(), Set.of(1))).thenReturn(List.of());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, null, null, null, Set.of(1)), userDetails))
				.withMessage("One or more tags not found in remove list");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenConflictingGarments_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(garment1), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(garmentRepository.findAllById(Set.of(1))).thenReturn(List.of(garment1));
		Mockito.when(garmentRepository.findAllByOutfitIdAndIdsIn(outfit1.getId(), Set.of(1))).thenReturn(List.of(garment1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, Set.of(1), Set.of(1), null, null),
								userDetails))
				.withMessage("One or more garments already in outfit");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenConflictingTags_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of(tag1)));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of(tag1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, null, null, Set.of(1), null),
								userDetails))
				.withMessage("One or more tags already in outfit");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenAlreadyExistingGarment_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Garment garment1 = Mockito.spy(new Garment(user1, "garment 1", List.of("url1", "url2"), List.of()));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(garment1), Set.of()));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(garmentRepository.findAllById(Set.of(1))).thenReturn(List.of(garment1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, Set.of(1), null, null, null),
								userDetails))
				.withMessage("One or more garments already in outfit");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void givenAlreadyExistingTag_whenUpdateOutfit_thenExpectIllegalArgumentException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(), Set.of(tag1)));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));
		Mockito.when(tagRepository.findAllById(Set.of(1))).thenReturn(List.of(tag1));

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(
						() -> outfitService.updateOutfit(
								new OutfitUpdateRequestDTO(1, null, null, null, null, Set.of(1), null),
								userDetails))
				.withMessage("One or more tags already in outfit");

		Mockito.verify(outfitRepository, Mockito.times(1)).findById(any());
		Mockito.verify(tagRepository, Mockito.times(1)).findAllById(any());
		Mockito.verify(tagRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllById(any());
		Mockito.verify(garmentRepository, Mockito.never()).findAllByOutfitIdAndIdsIn(any(), any());
		Mockito.verify(outfitRepository, Mockito.never()).save(any());
	}

	@Test
	public void testDeleteOutfit() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(),
						Set.of(tag1)));
		CustomUserDetails userDetails = getUserDetails(1, "user1", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);
		Mockito.when(user1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));

		assertThatNoException().isThrownBy(() -> outfitService.deleteOutfit(outfit1.getId(), userDetails));

		Mockito.verify(outfitRepository, Mockito.times(1)).deleteOutfitFromGarments(any());
		Mockito.verify(outfitRepository, Mockito.times(1)).deleteById(any());
	}

	@Test
	public void testDeleteOutfitAndExpectAccessDeniedException() {
		User user1 = Mockito.spy(new User("user1", "user1@test.com", "password1", null, Set.of()));
		Tag tag1 = Mockito.spy(new Tag("tag1"));
		Outfit outfit1 = Mockito
				.spy(new Outfit(user1, "outfit 1", "outfit 1 desc", LocalDateTime.now(), Set.of(),
						Set.of(tag1)));
		CustomUserDetails userDetails = getUserDetails(2, "user2", "", null);
		Mockito.when(outfit1.getId()).thenReturn(1);

		Mockito.when(outfitRepository.findById(outfit1.getId())).thenReturn(Optional.of(outfit1));

		assertThatExceptionOfType(AccessDeniedException.class)
				.isThrownBy(() -> outfitService.deleteOutfit(outfit1.getId(), userDetails))
				.withMessage("User does not have permission to delete this outfit");

		Mockito.verify(outfitRepository, Mockito.never()).deleteOutfitFromGarments(any());
		Mockito.verify(outfitRepository, Mockito.never()).deleteById(any());
	}

	@Test
	public void testDeleteOutfitAndExpectEntityNotFoundException() {
		Mockito.when(outfitRepository.findById(1)).thenReturn(Optional.empty());

		assertThatExceptionOfType(EntityNotFoundException.class)
				.isThrownBy(() -> outfitService.deleteOutfit(1, null))
				.withMessage("Outfit not found with ID: 1");

		Mockito.verify(outfitRepository, Mockito.never()).deleteOutfitFromGarments(any());
	}

}
