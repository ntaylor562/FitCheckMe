package com.fitcheckme.FitCheckMe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.models.Following;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Value("${fitcheckme.max-username-length}")
	private Integer maxUsernameLength;

	@Value("${fitcheckme.max-email-length}")
	private Integer maxEmailLength;

	@Value("${fitcheckme.max-user-bio-length}")
	private Integer maxBioLength;
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, @Value("${fitcheckme.bcrypt-password-encoder-strength}") Integer bCryptPasswordEncoderStrength) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder(bCryptPasswordEncoderStrength);
	}

	public List<UserRequestDTO> getAll() {
		return userRepository.findAllByOrderByIdAsc().stream().map((user) -> UserRequestDTO.toDTO(user)).toList();
	}

	public UserRequestDTO getById(Integer id) {
		return UserRequestDTO.toDTO(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id)))));
	}

	private User getUserById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
	
	}

	public UserRequestDTO getByUsername(String username) {
		User res = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with username: %s", String.valueOf(username))));
		return UserRequestDTO.toDTO(res);
	}

	public boolean exists(Integer id) {
		return userRepository.existsById(id);
	}

	public UserRequestDTO createUser(UserCreateRequestDTO user) {
		if(user.username().length() > this.maxUsernameLength) {
			throw new IllegalArgumentException(String.format("Username name must be at most %d characters", this.maxUsernameLength));
		}
		//Checking if username already exists
		if(userRepository.existsByUsernameIgnoreCase(user.username())) {
			throw new DataIntegrityViolationException(String.format("Username '%s' is taken", user.username()));
		}
		User newUser = new User(user.username(), user.email().toLowerCase(), passwordEncoder.encode(user.password()), null);
		this.userRepository.save(newUser);
		return UserRequestDTO.toDTO(newUser);
	}

	public void updateUserDetails(UserUpdateDetailsRequestDTO user) {
		if(user.username() != null && user.username().length() > this.maxUsernameLength) {
			throw new IllegalArgumentException(String.format("Username name must be at most %d characters", this.maxUsernameLength));
		}
		if(user.bio() != null && user.bio().length() > this.maxBioLength) {
			throw new IllegalArgumentException(String.format("User bio must be at most %d characters", this.maxBioLength));
		}
		//Checking if username already exists
		if(userRepository.existsByUsernameIgnoreCase(user.username())) {
			throw new DataIntegrityViolationException(String.format("Username '%s' is taken", user.username()));
		}
		User currentUser = this.getUserById(user.userId());
		if(user.username() != null) {
			if(!isValidUsername(user.username())) {
				throw new IllegalArgumentException("Username must only contain letters, numbers, and underscores");
			}
			currentUser.setUsername(user.username().toLowerCase());
		}
		if(user.bio() != null) {
			currentUser.setBio(user.bio());
		}
		
		this.userRepository.save(currentUser);
	}

	public void updatePassword(UserUpdatePasswordRequestDTO user, UserDetails userDetails) {
		User currentUser = this.getUserById(user.userId());
		if(!currentUser.getUsername().equals(userDetails.getUsername())) {
			throw new IllegalArgumentException("User does not have permission to update password");
		}
		if(!passwordEncoder.matches(user.oldPassword(), currentUser.getPassword())) {
			throw new IllegalArgumentException("Old password is incorrect");
		}
		currentUser.setPassword(passwordEncoder.encode(user.newPassword()));
		this.userRepository.save(currentUser);
	}

	//TODO add auth to make sure following can only be created by the follower
	public void followUser(Integer followerId, Integer followeeId) {
		User follower = this.getUserById(followerId);
		User followee = this.getUserById(followeeId);

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

	private boolean isValidUsername(String username) {
		return username != null && !username.isBlank() && username.matches("^[a-zA-Z0-9_]*$");
	}
}
