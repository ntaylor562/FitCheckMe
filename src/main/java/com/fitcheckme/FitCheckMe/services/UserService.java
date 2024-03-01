package com.fitcheckme.FitCheckMe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.Following;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	@Value("${fitcheckme.max-username-length}")
	private int maxUsernameLength;

	@Value("${fitcheckme.max-user-bio-length}")
	private int maxBioLength;
	
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public User getById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
	}

	public User getByUsername(String username) {
		User res = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with username: %s", String.valueOf(username))));
		return res;
	}

	public boolean exists(Integer id) {
		return userRepository.existsById(id);
	}

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
		User currentUser = this.getById(user.userId());
		currentUser.setUsername(user.username().toLowerCase());
		currentUser.setBio(user.bio() != "" ? user.bio() : null);
		
		this.userRepository.save(currentUser);
	}

	//TODO add auth to make sure following can only be created by the follower
	public void followUser(Integer followerId, Integer followeeId) {
		User follower = this.getById(followerId);
		User followee = this.getById(followeeId);

		if(follower.equals(followee)) {
			throw new IllegalArgumentException("User cannot follow themselves");
		}

		if(follower.getFollowing().contains(new Following(follower, followee))) {
			throw new DataIntegrityViolationException("User is already following this user");
		}

		follower.addFollowee(followee);
		followee.addFollower(follower);
		this.userRepository.save(follower);
		this.userRepository.save(followee);
	}

	//TODO edit this to delete all outfits and garments associated with the user and other actions that need to be performed when deleting an entity
	public void deleteUser(Integer id) {
		//this.getById(id);
		this.userRepository.deleteById(id);
	}
}
