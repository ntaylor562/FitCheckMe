package com.fitcheckme.FitCheckMe.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "garment")
public record Garment(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "garment_id")
	Integer garmentId,

	@ManyToMany(mappedBy = "outfit_id")
	List<Outfit> outfits
) {}
