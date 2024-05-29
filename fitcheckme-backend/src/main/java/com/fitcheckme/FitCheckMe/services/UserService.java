package com.fitcheckme.FitCheckMe.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.models.Following;
import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.GarmentRepository;
import com.fitcheckme.FitCheckMe.repositories.OutfitRepository;
import com.fitcheckme.FitCheckMe.repositories.RefreshTokenRepository;
import com.fitcheckme.FitCheckMe.repositories.RoleRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

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
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final OutfitRepository outfitRepository;
	private final GarmentRepository garmentRepository;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, OutfitRepository outfitRepository, GarmentRepository garmentRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.outfitRepository = outfitRepository;
		this.garmentRepository = garmentRepository;
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER_ADMIN')")
	public List<UserRequestDTO> getAll() {
		return userRepository.findAllByOrderByIdAsc().stream().map((user) -> UserRequestDTO.toDTO(user)).toList();
	}

	public UserRequestDTO getById(Integer id) throws EntityNotFoundException {
		return UserRequestDTO.toDTO(userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id)))));
	}

	@Transactional
	private User getUserById(Integer id) throws EntityNotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
	}

	public UserRequestDTO getByUsername(String username) throws EntityNotFoundException {
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
		if(!isValidUsername(user.username())) {
			throw new IllegalArgumentException("Username must only contain letters, numbers, and underscores");
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

		User newUser = new User(user.username(), user.email(), passwordEncoder.encode(user.password()), null, Set.of(roleRepository.findByRoleName("USER").get()));

		return UserRequestDTO.toDTO(this.userRepository.save(newUser));
	}

	public UserRequestDTO updateUserDetails(UserUpdateDetailsRequestDTO user, CustomUserDetails userDetails) throws DataIntegrityViolationException, IllegalArgumentException, AccessDeniedException {
		if(user.username() != null) {
			if(user.username().length() > this.maxUsernameLength) {
				throw new IllegalArgumentException(String.format("Username name must be at most %d characters", this.maxUsernameLength));
			}
			if(!isValidUsername(user.username())) {
				throw new IllegalArgumentException("Username must only contain letters, numbers, and underscores");
			}
			if(userRepository.existsByUsernameIgnoreCase(user.username())) {
				throw new DataIntegrityViolationException(String.format("Username '%s' is taken", user.username()));
			}
		}
		if(user.bio() != null && user.bio().length() > this.maxBioLength) {
			throw new IllegalArgumentException(String.format("User bio must be at most %d characters", this.maxBioLength));
		}

		User currentUser = this.getUserById(user.userId());
		if(currentUser.getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to update details");
		}

		if(user.username() != null) {
			currentUser.setUsername(user.username());
		}
		if(user.bio() != null) {
			currentUser.setBio(user.bio());
		}
		
		return UserRequestDTO.toDTO(this.userRepository.save(currentUser));
	}

	public void updatePassword(UserUpdatePasswordRequestDTO user, CustomUserDetails userDetails) throws IllegalArgumentException {
		User currentUser = this.getUserById(user.userId());
		if(currentUser.getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to update password");
		}
		if(!passwordEncoder.matches(user.oldPassword(), currentUser.getPassword())) {
			throw new IllegalArgumentException("Old password is incorrect");
		}
		currentUser.setPassword(passwordEncoder.encode(user.newPassword()));
		this.userRepository.save(currentUser);
	}

	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public void addUserRole(UserRoleUpdateDTO userRole) throws EntityNotFoundException {
		User user = this.getUserById(userRole.userId());
		Role role = roleRepository.findByRoleNameIgnoreCase(userRole.role()).orElseThrow(() -> new EntityNotFoundException(String.format("Role not found with name: %s", userRole.role())));
		user.addRole(role);
		this.userRepository.save(user);
	}

	@PreAuthorize("hasRole('SUPER_ADMIN')")
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

	@Transactional
	public void deleteUser(Integer id, CustomUserDetails userDetails) throws AccessDeniedException, EntityNotFoundException {
		User currentUser = this.getUserById(id);

		if(currentUser.getId() != userDetails.getUserId()) {
			throw new AccessDeniedException("User does not have permission to delete user");
		}

		List<Integer> garmentIds = garmentRepository.findByUserId(id).stream().map(g -> g.getId()).toList();
		List<Integer> outfitIds = outfitRepository.findByUserId(id).stream().map(o -> o.getId()).toList();
		//TODO when following is implemented, delete all followings
		this.refreshTokenRepository.deleteAllByUserId(id);

		this.garmentRepository.deleteAllGarmentURLsByGarmentIds(garmentIds);
		this.garmentRepository.deleteAllGarmentTagsByGarmentIds(garmentIds);
		this.garmentRepository.deleteAllGarmentsFromOutfits(garmentIds);
		this.garmentRepository.deleteAllByUserId(id);

		this.outfitRepository.deleteAllOutfitsFromGarments(outfitIds);
		this.outfitRepository.deleteAllOutfitTagsByOutfitIds(outfitIds);
		this.outfitRepository.deleteAllByUserId(id);

		this.userRepository.deleteById(id);
	}

	private boolean isValidUsername(String username) {
		return username != null 
			&& !username.isBlank() 
			&& username.matches("^[a-zA-Z0-9_]*$")
			&& username.length() <= this.maxUsernameLength;
	}
}
