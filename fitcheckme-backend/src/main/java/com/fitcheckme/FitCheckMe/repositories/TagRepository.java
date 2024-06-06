package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitcheckme.FitCheckMe.models.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer>{
	boolean existsByTagNameIgnoreCase(String tagName);
	
	Optional<Tag> findByTagNameIgnoreCase(String tagName);
	List<Tag> findAllByOrderByIdAsc();

	@Query("SELECT t FROM Outfit o JOIN o.outfitTags t WHERE o.id = :outfitId AND t.id IN :tagIds")
	List<Tag> findAllByOutfitIdAndIdsIn(Integer outfitId, Iterable<Integer> tagIds);

	@Query("SELECT t FROM Garment g JOIN g.garmentTags t WHERE g.id = :garmentId AND t.id IN :tagIds")
	List<Tag> findAllByGarmentIdAndIdsIn(Integer garmentId, Iterable<Integer> tagIds);

	@Modifying
	@Query(value = "DELETE FROM app.garment_tag WHERE tag_id = ?1", nativeQuery = true)
	void deleteGarmentTags(Integer tagId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_tag WHERE tag_id = ?1", nativeQuery = true)
	void deleteOutfitTags(Integer tagId);
}
