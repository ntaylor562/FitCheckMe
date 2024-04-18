package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Integer> {
	List<Garment> findByUserId(Integer userId);
	List<Garment> findAllByOrderByIdAsc();
}
