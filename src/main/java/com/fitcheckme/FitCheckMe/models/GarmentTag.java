package com.fitcheckme.FitCheckMe.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
public record GarmentTag(
	@Id
	@NotBlank
	Integer id,
	@NotBlank
	String tagName
) {}
