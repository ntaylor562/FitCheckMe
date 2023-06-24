package com.fitcheckme.FitCheckMe;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Outfit.OutfitCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.Tag.TagCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.services.OutfitService;
import com.fitcheckme.FitCheckMe.services.TagService;
import com.fitcheckme.FitCheckMe.services.UserService;

@Profile("dev")
@Component
public class DevInitialDataLoader implements CommandLineRunner {
	@Autowired
	private UserService userService;

	@Autowired
	private TagService tagService;

	@Autowired
	private OutfitService outfitService;

	@Override
	public void run(String... args) {
		System.out.println("DEV INITIAL DATA LOADER");
		if(userService.getAll().size() == 0) {
			System.out.println("CREATING USERS");
			userService.createUser(new UserCreateRequestDTO("bender", "test bio"));
			userService.createUser(new UserCreateRequestDTO("bender2", "TEST BIO 2"));
		}
		System.out.println("HERE1");

		if(tagService.getAll().size() == 0) {
			System.out.println("CREATING TAGS");
			tagService.createTag(new TagCreateRequestDTO("green"));
			tagService.createTag(new TagCreateRequestDTO("summer"));
		}
		System.out.println("HERE2");
		System.out.println(outfitService.getAll().size());
		System.out.println("HERE2.5");


		if(outfitService.getAll().size() != 0) {
			System.out.println("CREATING OUTFITS");
			// outfitService.createOutfit(new OutfitCreateRequestDTO(
			// 	userService.getByUsername("bender").getId(), 
			// 	"outfit1", 
			// 	"desc1", 
			// 	List.of(tagService.getByTagName("green").getId(), tagService.getByTagName("summer").getId()), 
			// 	List.of(new GarmentCreateRequestDTO("shirt1", userService.getByUsername("bender").getId(), List.of("url1", "url2"), List.of(tagService.getByTagName("green").getId()))),
			// 	new ArrayList<Integer>()
			// ));

			// outfitService.createOutfit(new OutfitCreateRequestDTO(
			// 	userService.getByUsername("bender2").getId(), 
			// 	"Outfit2", 
			// 	"DESC2", 
			// 	List.of(tagService.getByTagName("summer").getId()), 
			// 	List.of(new GarmentCreateRequestDTO("shirt2", userService.getByUsername("bender2").getId(), List.of("url3"), List.of(tagService.getByTagName("green").getId(), tagService.getByTagName("summer").getId()))),
			// 	new ArrayList<Integer>()
			// ));

			outfitService.createOutfit(new OutfitCreateRequestDTO(
				userService.getByUsername("bender").getId(), 
				"OUTFIT3", 
				"Desc3", 
				List.of(tagService.getByTagName("summer").getId()), 
				List.of(new GarmentCreateRequestDTO("shirt3", userService.getByUsername("bender").getId(), List.of("url4", "url5"), List.of(tagService.getByTagName("summer").getId()))),
				List.of(userService.getByUsername("bender").getOutfits().get(0).getGarments().get(0).getId())
			));
		}
		System.out.println("HERE3");

	}
}
