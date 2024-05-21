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

	@Modifying
	@Query(value = "DELETE FROM app.garment_tag WHERE tag_id = ?1", nativeQuery = true)
	void deleteGarmentTags(Integer tagId);

	@Modifying
	@Query(value = "DELETE FROM app.outfit_tag WHERE tag_id = ?1", nativeQuery = true)
	void deleteOutfitTags(Integer tagId);
}
