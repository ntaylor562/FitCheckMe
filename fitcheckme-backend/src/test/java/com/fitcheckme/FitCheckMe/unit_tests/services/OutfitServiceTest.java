package com.fitcheckme.FitCheckMe.unit_tests.services;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.fitcheckme.FitCheckMe.models.Garment;
import com.fitcheckme.FitCheckMe.models.Outfit;
import com.fitcheckme.FitCheckMe.models.Tag;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.services.OutfitService;

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

	@Test
	public void testGetAllAndExpectListOfOutfits() {
		
	}
}
