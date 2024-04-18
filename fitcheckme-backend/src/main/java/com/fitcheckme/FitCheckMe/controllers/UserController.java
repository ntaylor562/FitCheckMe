package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

//TODO implement permissions for this service
//TODO extract following to a social service
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("all")
	public List<UserRequestDTO> getAll() {
		return this.userService.getAll().stream().map(user -> UserRequestDTO.toDTO(user)).toList();
	}

	@GetMapping("{id}")
	public UserRequestDTO getById(@PathVariable Integer id) {
		try {
			return UserRequestDTO.toDTO(this.userService.getById(id));
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of user not found, could not get");
		}
	}

	@GetMapping("")
	public UserRequestDTO getByUsername(@RequestParam(value="username") String username) {
		try {
			return UserRequestDTO.toDTO(this.userService.getByUsername(username));
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found, could not get");
		}
	}

	//TODO add auth
	@PostMapping("")
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

	//TODO add auth
	@PutMapping("")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void updateUser(@Valid @RequestBody UserUpdateRequestDTO user) {
		try {
			userService.updateUser(user);
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

	//TODO add auth
	@DeleteMapping("{id}")
	public void deleteUser(@PathVariable Integer id) {
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
