package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer>{
	boolean existsByTagNameIgnoreCase(String tagName);
	
	Optional<Tag> findByTagNameIgnoreCase(String tagName);
	List<Tag> findAllByOrderByIdAsc();
}
