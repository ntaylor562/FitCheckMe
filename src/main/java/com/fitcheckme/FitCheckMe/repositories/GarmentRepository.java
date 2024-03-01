package com.fitcheckme.FitCheckMe.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Integer> {
	Collection<Garment> findByUserId(Integer userId);
}
