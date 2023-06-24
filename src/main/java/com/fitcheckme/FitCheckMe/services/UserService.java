package com.fitcheckme.FitCheckMe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {
	@Value("${fitcheckme.max-username-length}")
	private int maxUsernameLength;

	@Value("${fitcheckme.max-user-bio-length}")
	private int maxBioLength;
	
	@Autowired
	private UserRepository userRepository;

	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Transactional
	public User getById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
	}

	@Transactional
	public User getByUsername(String username) {
		User res = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with username: %s", String.valueOf(username))));
		
		System.out.println("USERS OUTFITS: ");
		System.out.println(res.getOutfits());
		System.out.println(res.getGarments());
		return res;
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
		User newUser = new User(user.username().toLowerCase(), user.bio());
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
		User currentUser = userRepository.findById(user.userId()).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(user.userId()))));
		currentUser.setUsername(user.username().toLowerCase());
		currentUser.setBio(user.bio());
		
		this.userRepository.save(currentUser);
	}

	public void deleteUser(Integer id) {
		userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
		this.userRepository.deleteById(id);
	}
}
