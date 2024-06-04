package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.GarmentImage;
import com.fitcheckme.FitCheckMe.models.composite_keys.GarmentImageId;

public interface GarmentImageRepository extends JpaRepository<GarmentImage, GarmentImageId> {
	Optional<GarmentImage> findByGarment_GarmentId(Integer garmentId);

	void deleteByGarment_GarmentIdAndImage_ImageFileId(Integer garmentId, Integer imageId);
}