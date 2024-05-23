package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitcheckme.FitCheckMe.models.Outfit;

public interface OutfitRepository extends JpaRepository<Outfit, Integer> {
	public List<Outfit> findByUserId(Integer userId);
	public List<Outfit> findByUser_UsernameIgnoreCase(String username);
	public List<Outfit> findAllByOrderByIdAsc();

	public List<Outfit> findAllByGarments_GarmentId(Integer garmentId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit WHERE user_id = ?1", nativeQuery = true)
	public void deleteAllByUserId(Integer userId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_garment WHERE outfit_id = ?1", nativeQuery = true)
	public void deleteOutfitFromGarments(Integer outfitId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_garment WHERE outfit_id IN ?1", nativeQuery = true)
	public void deleteAllOutfitsFromGarments(Iterable<Integer> outfitIds);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_tag WHERE outfit_id IN ?1", nativeQuery = true)
	public void deleteAllOutfitTagsByOutfitIds(Iterable<Integer> outfitIds);
}
