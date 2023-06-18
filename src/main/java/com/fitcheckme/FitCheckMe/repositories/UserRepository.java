package com.fitcheckme.FitCheckMe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUsername(String username);
}
