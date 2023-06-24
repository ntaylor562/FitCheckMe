package com.fitcheckme.FitCheckMe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Garment;

public interface GarmentRepository extends JpaRepository<Garment, Integer> {}
