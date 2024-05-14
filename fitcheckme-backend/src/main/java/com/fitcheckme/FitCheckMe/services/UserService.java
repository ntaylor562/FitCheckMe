package com.fitcheckme.FitCheckMe.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.models.Following;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.RoleRepository;
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
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, @Value("${fitcheckme.bcrypt-password-encoder-strength}") Integer bCryptPasswordEncoderStrength) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = new BCryptPasswordEncoder(bCryptPasswordEncoderStrength);
	}

	public List<UserRequestDTO> getAll() {
		return userRepository.findAllByOrderByIdAsc().stream().map((user) -> UserRequestDTO.toDTO(user)).toList();
	}

	public UserRequestDTO getById(Integer id) throws EntityNotFoundException {
		return UserRequestDTO.toDTO(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id)))));
	}

	private User getUserById(Integer id) throws EntityNotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
	
	}

	public UserRequestDTO getByUsername(String username) throws EntityNotFoundException {
		if(!isValidUsername(username)) {
			throw new EntityNotFoundException(String.format("User not found with username: %s", String.valueOf(username)));
		}
		User res = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with username: %s", String.valueOf(username))));
		return UserRequestDTO.toDTO(res);
	}

	public boolean exists(Integer id) {
		return userRepository.existsById(id);
	}

	public UserRequestDTO createUser(UserCreateRequestDTO user) throws DataIntegrityViolationException, IllegalArgumentException {
		if(user.username().length() > this.maxUsernameLength) {
			throw new IllegalArgumentException(String.format("Username name must be at most %d characters", this.maxUsernameLength));
		}
		if (user.email().length() > this.maxEmailLength) {
			throw new IllegalArgumentException(String.format("Email must be at most %d characters", this.maxEmailLength));
		}
		//Checking if username already exists
		if(userRepository.existsByUsernameIgnoreCase(user.username())) {
			throw new DataIntegrityViolationException(String.format("Username '%s' is taken", user.username()));
		}
		//Checking if email already exists
		if(userRepository.existsByEmailIgnoreCase(user.email())) {
			throw new DataIntegrityViolationException(String.format("Email '%s' is taken", user.email()));
		}
		User newUser = new User(user.username(), user.email().toLowerCase(), passwordEncoder.encode(user.password()), null, Set.of(roleRepository.findByRoleName("USER").get()));
		this.userRepository.save(newUser);
		return UserRequestDTO.toDTO(newUser);
	}

	public void updateUserDetails(UserUpdateDetailsRequestDTO user, UserDetails userDetails) throws DataIntegrityViolationException, IllegalArgumentException {
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
		if(!currentUser.getUsername().equals(userDetails.getUsername())) {
			throw new IllegalArgumentException("User does not have permission to update details");
		}
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

	public void updatePassword(UserUpdatePasswordRequestDTO user, UserDetails userDetails) throws IllegalArgumentException {
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

	public void addUserRole(UserRoleUpdateDTO userRole) throws EntityNotFoundException {
		User user = this.getUserById(userRole.userId());
		Role role = roleRepository.findByRoleNameIgnoreCase(userRole.role()).orElseThrow(() -> new EntityNotFoundException(String.format("Role not found with name: %s", userRole.role())));
		user.addRole(role);
		this.userRepository.save(user);
	}

	public void removeUserRole(UserRoleUpdateDTO userRole) throws EntityNotFoundException {
		User user = this.getUserById(userRole.userId());
		Role role = roleRepository.findByRoleNameIgnoreCase(userRole.role()).orElseThrow(() -> new EntityNotFoundException(String.format("Role not found with name: %s", userRole.role())));
		user.removeRole(role);
		this.userRepository.save(user);
	}

	//TODO add auth to make sure following can only be created by the follower
	public void followUser(Integer followerId, Integer followeeId) throws DataIntegrityViolationException, IllegalArgumentException {
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
		return username != null 
			&& !username.isBlank() 
			&& username.matches("^[a-zA-Z0-9_]*$")
			&& username.length() <= this.maxUsernameLength;
	}
}
