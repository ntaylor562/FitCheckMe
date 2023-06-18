package com.fitcheckme.FitCheckMe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fitcheckme.FitCheckMe.DTOs.User.UserCreateRequestDTO;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.services.UserService;

import jakarta.persistence.EntityNotFoundException;

//TODO implement permissions for this service
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
	@Autowired
	private UserService userService;

	//Retrieve all users
	@GetMapping("")
	public List<User> findAll() {
		return this.userService.getAll();
	}

	//Retrieve a user by ID
	@GetMapping("{id}")
	public User findById(@PathVariable Long id) {
		try {
			return this.userService.getById(id);
		}
		catch(EntityNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID of user not found, could not get");
		}
	}

	//Create a user
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public void createUser(@RequestBody UserCreateRequestDTO user) {
		try {
			this.userService.createUser(user);
		}
		catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "ID of user not found, could not get");
		}
	}

	//Update a user
	//TODO Make sure to use a try catch for both exception types on this one
}
