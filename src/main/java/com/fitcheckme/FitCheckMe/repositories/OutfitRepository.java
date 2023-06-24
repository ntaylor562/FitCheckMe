package com.fitcheckme.FitCheckMe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Outfit;

public interface OutfitRepository extends JpaRepository<Outfit, Integer> {}
