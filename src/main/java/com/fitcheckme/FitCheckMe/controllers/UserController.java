package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.User.UserUpdateRequestDTO;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.UserService;
import com.fitcheckme.FitCheckMe.services.get_services.UserGetService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

//TODO implement permissions for this service
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
	@Autowired
	private UserGetService userGetService;

	@Autowired
	private UserService userService;

	@GetMapping("")
	public List<User> findAll() {
		return this.userGetService.getAll();
	}

	@GetMapping("{id}")
	public User findById(@PathVariable Integer id) {
		try {
			return this.userGetService.getById(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of user not found, could not get");
		}
	}

	@GetMapping("{username}")
	public User findByUsername(@PathVariable String username) {
		try {
			return this.userGetService.getByUsername(username);
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
		catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
	}

	//TODO add auth
	@DeleteMapping("{id}")
	public void deleteUser(@PathVariable Integer id) {
		try {
			userService.deleteUser(id);
		}
		catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
