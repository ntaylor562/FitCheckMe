package com.fitcheckme.FitCheckMe.integration_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

public class TagControllerIntegrationTest extends AbstractIntegrationTest {
	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GarmentRepository garmentRepository;

	@Autowired
	private OutfitRepository outfitRepository;

	@BeforeEach
	public void setup() {
		garmentRepository.deleteAll();
		outfitRepository.deleteAll();
		tagRepository.deleteAll();

		resetAuth();
	}

	@Test
	public void testGetAllTagsAndExpectListOfTags() {
		Tag tag1 = tagRepository.save(new Tag("tag1"));
		Tag tag2 = tagRepository.save(new Tag("tag2"));

		ResponseEntity<Object> response = getCall("/api/tag/all");
		List<TagRequestDTO> tags = getListOfObjectsFromResponse(response, TagRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(tags).containsExactly(TagRequestDTO.toDTO(tag1), TagRequestDTO.toDTO(tag2));
	}

	// @Test //TODO Add tag getall to list of unauthenticated paths
	// public void testGetAllTagsUnauthenticatedAndExpectListOfTags() {
	// 	Tag tag1 = tagRepository.save(new Tag("tag1"));
	// 	Tag tag2 = tagRepository.save(new Tag("tag2"));

	// 	logout();
	// 	ResponseEntity<Object> response = getCall("/api/tag/all");
	// 	List<TagRequestDTO> tags = getListOfObjectsFromResponse(response, TagRequestDTO.class);

	// 	assertThat(response.getStatusCode().isError()).isFalse();
	// 	assertThat(tags).containsExactly(TagRequestDTO.toDTO(tag1), TagRequestDTO.toDTO(tag2));
	// }

	@Test
	public void testGetTagByIdAndExpectTag() {
		Tag tag = tagRepository.save(new Tag("tag"));

		ResponseEntity<Object> response = getCall(String.format("/api/tag?id=%d", tag.getId()));
		TagRequestDTO tagDTO = getObjectFromResponse(response, TagRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(tagDTO).isEqualTo(TagRequestDTO.toDTO(tag));
	}

	@Test
	public void testGetTagByIdAndExpectNotFound() {
		ResponseEntity<Object> response = getCall("/api/tag?id=1", true);

		assertThat(response.getStatusCode().isError()).isTrue();
		assertThat(getExceptionResponseFromResponse(response).message()).isEqualTo("Tag not found with ID: 1");
	}

	@Test
	public void testGetTagByNameAndExpectTag() {
		Tag tag = tagRepository.save(new Tag("tag"));

		ResponseEntity<Object> response = getCall(String.format("/api/tag?name=%s", tag.getName()));
		TagRequestDTO tagDTO = getObjectFromResponse(response, TagRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(tagDTO).isEqualTo(TagRequestDTO.toDTO(tag));
	}

	@Test
	public void testGetTagByNameAndExpectNotFound() {
		ResponseEntity<Object> response = getCall("/api/tag?name=tag", true);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(getExceptionResponseFromResponse(response).message()).isEqualTo("Tag not found with name: tag");
	}

	@Test
	public void testCreateTagAndExpectTag() {
		logout();
		login("test_super_admin");

		TagCreateRequestDTO tagDTO = new TagCreateRequestDTO("tag");

		ResponseEntity<Object> response = postCall("/api/tag/create", tagDTO);
		TagRequestDTO createdTagDTO = getObjectFromResponse(response, TagRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(createdTagDTO).isEqualTo(TagRequestDTO.toDTO(tagRepository.findByTagNameIgnoreCase("tag").get()));
	}

	@Test
	public void testDeleteTag() {
		logout();
		login("test_super_admin");
		User user = userRepository.findByUsernameIgnoreCase("test_super_admin").get();

		Tag tag1 = tagRepository.save(new Tag("tag1"));

		Garment garment1 = garmentRepository.save(new Garment(user, "garment1", null, List.of(tag1)));

		outfitRepository.save(
				new Outfit(user, "outfit1", "outfit1", LocalDateTime.now(), List.of(garment1), List.of(tag1)));

		assertThat(garmentRepository.findByGarmentTags_TagId(tag1.getId()).size()).isEqualTo(1);
		assertThat(outfitRepository.findByOutfitTags_TagId(tag1.getId()).size()).isEqualTo(1);

		ResponseEntity<Object> response = deleteCall("/api/tag/delete?id=" + tag1.getId());
		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(tagRepository.findById(tag1.getId())).isEmpty();

		assertThat(garmentRepository.count()).isEqualTo(1);
		assertThat(outfitRepository.count()).isEqualTo(1);
		assertThat(garmentRepository.findByGarmentTags_TagId(tag1.getId()).size()).isEqualTo(0);
		assertThat(outfitRepository.findByOutfitTags_TagId(tag1.getId()).size()).isEqualTo(0);
	}

	@Test
	public void testDeleteTagAndExpectNotFound() {
		logout();
		login("test_super_admin");

		ResponseEntity<Object> response = deleteCall("/api/tag/delete?id=1", true);

		assertThat(response.getStatusCode().isError()).isTrue();
		assertThat(getExceptionResponseFromResponse(response).message()).isEqualTo("Tag not found with ID: 1");
	}
}
