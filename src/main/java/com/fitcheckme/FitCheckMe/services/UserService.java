package com.fitcheckme.FitCheckMe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.services.get_services.UserGetService;

//TODO make sure someone can't follow themselves and can't follow someone multiple times
@Service
public class UserService {
	@Value("${fitcheckme.max-username-length}")
	private int maxUsernameLength;

	@Value("${fitcheckme.max-user-bio-length}")
	private int maxBioLength;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserGetService userGetService;

	public User createUser(UserCreateRequestDTO user) {
		if(user.username().length() > this.maxUsernameLength) {
			throw new IllegalArgumentException(String.format("Username name must be at most %d characters", this.maxUsernameLength));
		}
		if(user.bio().length() > this.maxBioLength) {
			throw new IllegalArgumentException(String.format("User bio must be at most %d characters", this.maxBioLength));
		}
		//Checking if username already exists
		if(userRepository.existsByUsernameIgnoreCase(user.username())) {
			throw new DataIntegrityViolationException(String.format("Username '%s' is taken", user.username()));
		}
		User newUser = new User(user.username().toLowerCase(), user.bio() != "" ? user.bio() : null);
		this.userRepository.save(newUser);
		return newUser;
	}

	public void updateUser(UserUpdateRequestDTO user) {
		if(user.username().length() > this.maxUsernameLength) {
			throw new IllegalArgumentException(String.format("Username name must be at most %d characters", this.maxUsernameLength));
		}
		if(user.bio().length() > this.maxBioLength) {
			throw new IllegalArgumentException(String.format("User bio must be at most %d characters", this.maxBioLength));
		}
		//Checking if username already exists
		if(userRepository.existsByUsernameIgnoreCase(user.username())) {
			throw new DataIntegrityViolationException(String.format("Username '%s' is taken", user.username()));
		}
		User currentUser = userGetService.getById(user.userId());
		currentUser.setUsername(user.username().toLowerCase());
		currentUser.setBio(user.bio() != "" ? user.bio() : null);
		
		this.userRepository.save(currentUser);
	}

	//TODO edit this to delete all outfits and garments associated with the user and other actions that need to be performed when deleting an entity
	public void deleteUser(Integer id) {
		userGetService.getById(id);
		this.userRepository.deleteById(id);
	}
}
