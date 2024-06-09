package com.fitcheckme.FitCheckMe.integration_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;
import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentImageRepository;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.ImageFileRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

public class GarmentControllerIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private GarmentRepository garmentRepository;

	@Autowired
	private GarmentImageRepository garmentImageRepository;

	@Autowired
	private ImageFileRepository imageFileRepository;

	@Autowired
	private TagRepository tagRepository;

	private User user;
	private Tag tag1;
	private Tag tag2;

	@BeforeEach
	public void setup() {
		garmentRepository.deleteAll();
		garmentImageRepository.deleteAll();
		imageFileRepository.deleteAll();

		if(!tagRepository.existsByTagNameIgnoreCase("tag1")) {
			this.tag1 = tagRepository.save(new Tag("tag1"));
		}
		if(!tagRepository.existsByTagNameIgnoreCase("tag2")) {
			this.tag2 = tagRepository.save(new Tag("tag2"));
		}

		this.user = userRepository.findById(1).get();

		resetAuth();
	}

	@Test
	public void testGetAllGarmentsAndExpectListOfGarments() {
		logout();
		login("test_super_admin", "test");

		Garment garment1 = garmentRepository.save(new Garment(this.user, "garment 1", List.of("url1", "url2"), List.of(this.tag1, this.tag2)));
		Garment garment2 = garmentRepository.save(new Garment(this.user, "garment 2", List.of("url1"), List.of(this.tag1, this.tag2)));

		ResponseEntity<Object> response = getCall("/api/garment/all");
		List<GarmentRequestDTO> garmentDTOs = getListOfObjectsFromResponse(response, GarmentRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(garmentDTOs).containsExactly(GarmentRequestDTO.toDTO(garment1), GarmentRequestDTO.toDTO(garment2));
	}

	@Test
	public void testGetGarmentByIdAndExpectGarment() {
		Garment garment = garmentRepository.save(new Garment(this.user, "garment 1", List.of("url1", "url2"), List.of(this.tag1, this.tag2)));

		ResponseEntity<Object> response = getCall("/api/garment?id=" + garment.getId());
		GarmentRequestDTO garmentDTO = getObjectFromResponse(response, GarmentRequestDTO.class);

		assertThat(response.getStatusCode().isError()).isFalse();
		assertThat(garmentDTO).isEqualTo(GarmentRequestDTO.toDTO(garment));
	}

	
}
