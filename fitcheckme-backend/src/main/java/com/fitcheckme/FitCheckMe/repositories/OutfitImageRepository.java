package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.OutfitImage;

public interface OutfitImageRepository extends JpaRepository<OutfitImage, Integer> {
	Optional<OutfitImage> findByOutfit_OutfitId(Integer outfitId);
}