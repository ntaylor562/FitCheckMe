package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fitcheckme.FitCheckMe.models.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Integer> {
	List<Garment> findByUserId(Integer userId);

	List<Garment> findByUser_UsernameIgnoreCase(String username);

	List<Garment> findAllByOrderByIdAsc();

	@Query("SELECT g FROM Outfit o JOIN o.garments g WHERE o.id = :outfitId AND g.id IN :garmentIds")
	List<Garment> findAllByOutfitIdAndId(@Param("garmentIds") List<Integer> garmentIds,
			@Param("outfitId") Integer outfitId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_garment WHERE garment_id = ?1", nativeQuery = true)
	void deleteGarmentFromOutfits(Integer garmentId);
}
