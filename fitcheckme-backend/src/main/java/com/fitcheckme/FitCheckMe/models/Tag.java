package com.fitcheckme.FitCheckMe.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Tag", schema = "app")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer tagId;

	@NotBlank
	@Column(name = "tag_name", nullable = false)
	String tagName;

	public Tag() {

	}

	public Tag(String tagName) {
		this.tagName = tagName;
	}

	public Integer getId() {
		return this.tagId;
	}

	public String getName() {
		return this.tagName;
	}
}
