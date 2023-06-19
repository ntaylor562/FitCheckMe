package com.fitcheckme.FitCheckMe.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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

	@ManyToMany(mappedBy = "outfitTags")
	List<Outfit> outfits;

	@ManyToMany(mappedBy = "garmentTags")
	List<Garment> garments;

	public Tag() {

	}

	public Tag(String tagName) {
		this.tagName = tagName;
	}

	public Integer getId() {
		return this.tagId;
	}

	public String getTagName() {
		return this.tagName;
	}

	public List<Outfit> getOutfits() {
		return this.outfits;
	}

	public List<Garment> getGarments() {
		return this.garments;
	}
}
