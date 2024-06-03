package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.GarmentImage;

public interface GarmentImageRepository extends JpaRepository<GarmentImage, Integer> {
	Optional<GarmentImage> findByGarment_GarmentId(Integer garmentId);
}