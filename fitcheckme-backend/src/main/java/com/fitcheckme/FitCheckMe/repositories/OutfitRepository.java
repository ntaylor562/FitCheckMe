package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Outfit;

public interface OutfitRepository extends JpaRepository<Outfit, Integer> {
	public List<Outfit> findByUserId(Integer userId);
	public List<Outfit> findByUser_UsernameIgnoreCase(String username);
	public List<Outfit> findAllByOrderByIdAsc();
}
