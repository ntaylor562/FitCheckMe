package com.fitcheckme.FitCheckMe.services.get_services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserGetService {
	@Autowired
	private UserRepository userRepository;

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public User getById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with ID: %s", String.valueOf(id))));
	}

	public User getByUsername(String username) {
		User res = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new EntityNotFoundException(String.format("User not found with username: %s", String.valueOf(username))));
		
		System.out.println("USERS OUTFITS: ");
		System.out.println(res.getOutfits());
		System.out.println(res.getGarments());
		return res;
	}
}
