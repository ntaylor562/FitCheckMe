package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByUsernameIgnoreCase(String username);

	Optional<User> findByUsernameIgnoreCase(String username);
}
