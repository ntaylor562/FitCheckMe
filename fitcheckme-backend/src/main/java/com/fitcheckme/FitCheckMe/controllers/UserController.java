package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("all")
	public List<UserRequestDTO> getAll() {
		return this.userService.getAll();
	}

	@GetMapping("")
	public UserRequestDTO getUser(@RequestParam(required = false) Integer id, @RequestParam(required = false) String username) {
		try {
			if(id != null) {
				return this.userService.getById(id);
			}
			if(username != null) {
				return this.userService.getByUsername(username);
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ID or username provided");
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
	}

	//TODO add auth
	@PostMapping("create")
	@ResponseStatus(HttpStatus.CREATED)
	public void createUser(@Valid @RequestBody UserCreateRequestDTO user) {
		try {
			this.userService.createUser(user);
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateUserDetails(@Valid @RequestBody UserUpdateDetailsRequestDTO user) {
		try {
			userService.updateUserDetails(user);
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PutMapping("password")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateUserPassword(@Valid @RequestBody UserUpdatePasswordRequestDTO user, @AuthenticationPrincipal UserDetails userDetails) {
		try {
			userService.updatePassword(user, userDetails);
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	//TODO add auth
	@DeleteMapping("")
	public void deleteUser(@RequestParam Integer id) {
		try {
			userService.deleteUser(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		catch(IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
