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

	List<Garment> findByGarmentTags_TagId(Integer tagId);

	@Query("SELECT g FROM Outfit o JOIN o.garments g WHERE o.id = :outfitId AND g.id IN :garmentIds")
	List<Garment> findAllByOutfitIdAndIdsIn(@Param("outfitId") Integer outfitId,
			@Param("garmentIds") Iterable<Integer> garmentIds);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_garment WHERE garment_id = ?1", nativeQuery = true)
	void deleteGarmentFromOutfits(Integer garmentId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_garment WHERE garment_id IN ?1", nativeQuery = true)
	void deleteAllGarmentsFromOutfits(Iterable<Integer> garmentIds);

	@Modifying
	@Query(value = "DELETE FROM app.garment WHERE user_id = ?1", nativeQuery = true)
	void deleteAllByUserId(Integer userId);

	@Modifying
	@Query(value = "DELETE FROM app.garment_url WHERE garment_id IN ?1", nativeQuery = true)
	void deleteAllGarmentURLsByGarmentIds(Iterable<Integer> garmentIds);

	@Modifying
	@Query(value = "DELETE FROM app.garment_tag WHERE garment_id IN ?1", nativeQuery = true)
	void deleteAllGarmentTagsByGarmentIds(Iterable<Integer> garmentIds);
}
