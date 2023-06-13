package com.fitcheckme.FitCheckMe.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Tag")
public record Tag(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer tagId,

	@NotBlank
	@Column(name = "tag_name")
	String tagName
) {}
