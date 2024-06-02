package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.ExceptionResponseDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRoleUpdateDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

//TODO implement permissions for this service
//TODO extract following to a social service
@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("all")
	public ResponseEntity<List<UserRequestDTO>> getAll() {
		return new ResponseEntity<List<UserRequestDTO>>(this.userService.getAll(), HttpStatus.OK);
	}

	@GetMapping("")
	public ResponseEntity<?> getUser(@RequestParam(required = false) Integer id, @RequestParam(required = false) String username) {
		try {
			if(id != null) {
				return new ResponseEntity<UserRequestDTO>(this.userService.getById(id), HttpStatus.OK);
			}
			if(username != null) {
				return new ResponseEntity<UserRequestDTO>(this.userService.getByUsername(username), HttpStatus.OK);
			}
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("No ID or username provided", "An ID or username is required"), HttpStatus.BAD_REQUEST);
		}
		catch(EntityNotFoundException e) {
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("User not found", "User was not found"), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("currentuser")
	public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			return new ResponseEntity<UserRequestDTO>(this.userService.getById(userDetails.getUserId()), HttpStatus.OK);
		}
		catch(EntityNotFoundException e) {
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("User not found", "User was not found"), HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("create")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequestDTO user) {
		try {
			return new ResponseEntity<UserRequestDTO>(this.userService.createUser(user), HttpStatus.CREATED);
		}
		catch(DataIntegrityViolationException e) {
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Error creating user", e.getMessage()), HttpStatus.CONFLICT);
		}
	}

	/*
	@PostMapping("{id}/follow/{followeeId}")
	@ResponseStatus(HttpStatus.CREATED)
	public void followUser(@PathVariable Integer followerId, @PathVariable Integer followeeId) {
		try {
			this.userService.followUser(followerId, followeeId);
		}
		catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	*/

	@PutMapping("details")
	public ResponseEntity<?> updateUserDetails(@Valid @RequestBody UserUpdateDetailsRequestDTO user, @AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			return new ResponseEntity<UserRequestDTO>(userService.updateUserDetails(user, userDetails), HttpStatus.OK);
		}
		catch(DataIntegrityViolationException e) {
			return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Error updating user details", e.getMessage()), HttpStatus.CONFLICT);
		}
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER_ADMIN')")
	@PutMapping("addrole")
	@ResponseStatus(HttpStatus.OK)
	public void addRole(@Valid @RequestBody UserRoleUpdateDTO userRole) {
		userService.addUserRole(userRole);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	@PutMapping("removerole")
	@ResponseStatus(HttpStatus.OK)
	public void removeRole(@Valid @RequestBody UserRoleUpdateDTO userRole) {
		userService.removeUserRole(userRole);
	}

	@PutMapping("password")
	@ResponseStatus(HttpStatus.OK)
	public void updateUserPassword(@Valid @RequestBody UserUpdatePasswordRequestDTO user, @AuthenticationPrincipal CustomUserDetails userDetails) {
		userService.updatePassword(user, userDetails);
	}

	@DeleteMapping("")
	@ResponseStatus(HttpStatus.OK)
	public void deleteUser(@RequestParam Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
		userService.deleteUser(id, userDetails);
	}
}
