package com.fitcheckme.FitCheckMe;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.TagRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

@Profile("dev")
@Component
public class DevInitialDataLoader implements CommandLineRunner {
	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	private final OutfitRepository outfitRepository;
	private final GarmentRepository garmentRepository;
	private final JdbcTemplate jdbcTemplate;


	public DevInitialDataLoader(UserRepository userRepository, TagRepository tagRepository, OutfitRepository outfitRepository, GarmentRepository garmentRepository, JdbcTemplate jdbcTemplate) {
		this.userRepository = userRepository;
		this.tagRepository = tagRepository;
		this.outfitRepository = outfitRepository;
		this.garmentRepository = garmentRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	private final String fileName = "data-dev.sql";
	
	@Override
	public void run(String... args) {
		//If the users, tags, and outfits are empty (supposed to be all tables but you know who has time to check all that)
		if(userRepository.count() == 0 && tagRepository.count() == 0 && outfitRepository.count() == 0 && garmentRepository.count() == 0) {
			Resource resource = new ClassPathResource(fileName);
			try {
				//Populate DB with sample data in data sql file
				jdbcTemplate.execute(FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
			}
			catch(Exception e) {
				System.out.println(String.format("ERROR reading file %s: %s", fileName, e.toString()));
				e.printStackTrace();
			}
		}
	}
}
