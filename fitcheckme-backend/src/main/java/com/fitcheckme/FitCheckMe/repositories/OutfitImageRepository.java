package com.fitcheckme.FitCheckMe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.OutfitImage;
import com.fitcheckme.FitCheckMe.models.composite_keys.OutfitImageId;

public interface OutfitImageRepository extends JpaRepository<OutfitImage, OutfitImageId> {
	Optional<OutfitImage> findByOutfit_OutfitId(Integer outfitId);

	void deleteByOutfit_OutfitIdAndImage_ImageFileId(Integer outfitId, Integer imageId);
}