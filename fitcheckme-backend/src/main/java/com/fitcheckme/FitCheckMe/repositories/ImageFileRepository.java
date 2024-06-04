package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.ImageFile;

public interface ImageFileRepository extends JpaRepository<ImageFile, Integer>{
	public List<ImageFile> findAllByIdIn(Iterable<Integer> imageIds);
}
