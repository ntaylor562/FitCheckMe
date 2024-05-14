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

import com.fitcheckme.FitCheckMe.controllers.OutfitController;
import com.fitcheckme.FitCheckMe.controllers.TagController;
import com.fitcheckme.FitCheckMe.controllers.UserController;

@Profile("dev")
@Component
public class DevInitialDataLoader implements CommandLineRunner {
	private final UserController userController;
	private final TagController tagController;
	private final OutfitController outfitController;
	private final JdbcTemplate jdbcTemplate;


	public DevInitialDataLoader(UserController userController, TagController tagController, OutfitController outfitController, JdbcTemplate jdbcTemplate) {
		this.userController = userController;
		this.tagController = tagController;
		this.outfitController = outfitController;
		this.jdbcTemplate = jdbcTemplate;
	}

	private final String fileName = "data-dev.sql";
	
	@Override
	public void run(String... args) {
		//If the users, tags, and outfits are empty (supposed to be all tables but you know who has time to check all that)
		if(userController.getAll().getBody().isEmpty() && tagController.getAll().getBody().isEmpty() && outfitController.getAll().getBody().isEmpty()) {
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
