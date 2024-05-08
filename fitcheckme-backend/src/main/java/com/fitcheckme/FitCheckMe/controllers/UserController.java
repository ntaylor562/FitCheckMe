package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateDetailsRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdatePasswordRequestDTO;
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
	@ResponseStatus(HttpStatus.OK)
	public List<UserRequestDTO> getAll() {
		return this.userService.getAll();
	}

	@GetMapping("")
	public ResponseEntity<?> getUser(@RequestParam(required = false) Integer id, @RequestParam(required = false) String username) {
		try {
			if(id != null) {
				return new ResponseEntity<>(this.userService.getById(id), HttpStatus.OK);
			}
			if(username != null) {
				return new ResponseEntity<>(this.userService.getByUsername(username), HttpStatus.OK);
			}
			return new ResponseEntity<>("No ID or username provided", HttpStatus.BAD_REQUEST);
		}
		catch(EntityNotFoundException e) {
			return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
		}
	}

	//TODO add auth
	@PostMapping("create")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequestDTO user) {
		try {
			this.userService.createUser(user);
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		catch(DataIntegrityViolationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
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

	//TODO add auth
	@PutMapping("details")
	public ResponseEntity<?> updateUserDetails(@Valid @RequestBody UserUpdateDetailsRequestDTO user, @AuthenticationPrincipal UserDetails userDetails) {
		try {
			userService.updateUserDetails(user, userDetails);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		catch(DataIntegrityViolationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PutMapping("password")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateUserPassword(@Valid @RequestBody UserUpdatePasswordRequestDTO user, @AuthenticationPrincipal UserDetails userDetails) {
		userService.updatePassword(user, userDetails);
	}

	//TODO add auth
	@DeleteMapping("")
	@ResponseStatus(HttpStatus.OK)
	public void deleteUser(@RequestParam Integer id) {
		userService.deleteUser(id);
	}
}
