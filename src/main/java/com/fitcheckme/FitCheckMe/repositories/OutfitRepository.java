package com.fitcheckme.FitCheckMe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitcheckme.FitCheckMe.models.Outfit;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {}
