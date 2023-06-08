package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public record Outfit (
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="outfit_id")
	Long id,
	List<Garment> garments,
	@NotBlank
	String desc,
	@NotBlank
	LocalDateTime dateCreated,
	@NotBlank
	LocalDateTime dateModified,
	List<Garment> tags
) {}
