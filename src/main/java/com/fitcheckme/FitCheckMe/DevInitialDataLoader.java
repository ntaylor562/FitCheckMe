package com.fitcheckme.FitCheckMe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.controllers.OutfitController;
import com.fitcheckme.FitCheckMe.controllers.TagController;
import com.fitcheckme.FitCheckMe.controllers.UserController;

import jakarta.transaction.Transactional;

@Profile("dev")
@Component
public class DevInitialDataLoader implements CommandLineRunner {
	@Autowired
	private UserController userController;

	@Autowired
	private TagController tagController;

	@Autowired
	private OutfitController outfitController;

	@Override
	@Transactional
	public void run(String... args) {
		System.out.println("DEV INITIAL DATA LOADER");
		if(userController.findAll().size() == 0) {
			System.out.println("CREATING USERS");
			userController.createUser(new UserCreateRequestDTO("bender", "test bio"));
			userController.createUser(new UserCreateRequestDTO("bender2", "TEST BIO 2"));
		}
		System.out.println("HERE1");

		if(tagController.findAll().size() == 0) {
			System.out.println("CREATING TAGS");
			tagController.createTag(new TagCreateRequestDTO("green"));
			tagController.createTag(new TagCreateRequestDTO("summer"));
		}
		System.out.println("HERE2");

		if(outfitController.findAll().size() == 0) {
			System.out.println("CREATING OUTFITS");
			outfitController.createOutfit(new OutfitCreateRequestDTO(
				userController.findByUsername("bender").getId(), 
				"outfit1", 
				"desc1", 
				List.of(tagController.findByTagName("green").getId(), tagController.findByTagName("summer").getId()), 
				List.of(new GarmentCreateRequestDTO("shirt1", userController.findByUsername("bender").getId(), List.of("url1", "url2"), List.of(tagController.findByTagName("green").getId()))),
				new ArrayList<Integer>()
			));

			outfitController.createOutfit(new OutfitCreateRequestDTO(
				userController.findByUsername("bender2").getId(), 
				"Outfit2", 
				"DESC2", 
				List.of(tagController.findByTagName("summer").getId()), 
				List.of(new GarmentCreateRequestDTO("shirt2", userController.findByUsername("bender2").getId(), List.of("url3"), List.of(tagController.findByTagName("green").getId(), tagController.findByTagName("summer").getId()))),
				new ArrayList<Integer>()
			));

			System.out.println(userController.findByUsername("bender").getUsername());
			System.out.println(userController.findByUsername("bender").getBio());
			System.out.println(userController.findByUsername("bender").getOutfits());

			//This one isn't working because these outfits are not being created in sequence. TODO make actual test cases so that these may run in sequence
			// outfitController.createOutfit(new OutfitCreateRequestDTO(
			// 	userController.findByUsername("bender").getId(), 
			// 	"OUTFIT3", 
			// 	"Desc3", 
			// 	List.of(tagController.findByTagName("summer").getId()), 
			// 	List.of(new GarmentCreateRequestDTO("shirt3", userController.findByUsername("bender").getId(), List.of("url4", "url5"), List.of(tagController.findByTagName("summer").getId()))),
			// 	List.of(userController.findByUsername("bender").getOutfits().get(0).getGarments().get(0).getId())
			// ));
		}
		System.out.println("HERE3");

	}
}
