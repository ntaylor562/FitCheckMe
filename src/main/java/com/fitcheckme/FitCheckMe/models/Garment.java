package com.fitcheckme.FitCheckMe.models;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "garment", schema = "app")
public class Garment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "garment_id")
	private Long garmentId;

	@Column(name = "garment_name")
	private String garmentName;

	@ManyToMany(mappedBy = "garments")
	private List<Outfit> outfits;

	@ElementCollection
	@CollectionTable(
		name = "garment_url",
		schema = "app",
		joinColumns = @JoinColumn(name = "garment_id")
	)
	@Column(name = "garment_url")
	private List<String> urls;

	@ManyToMany
	@JoinTable(
		name = "garment_tag",
		schema = "app",
		joinColumns = @JoinColumn(name = "garment_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
	)
	private List<Tag> garmentTags;

	public Garment() {

	}

	public Garment(String garmentName, List<String> urls, List<Tag> tags) {
		this.garmentName = garmentName;
		this.urls = urls;
		this.garmentTags = tags;
	}

	public Long getId() {
		return this.garmentId;
	}

	public String getName() {
		return this.garmentName;
	}

	public List<Outfit> getOutfits() {
		return this.outfits;
	}

	public List<String> getURLs() {
		return this.urls;
	}

	public List<Tag> getTags() {
		return this.garmentTags;
	}
}
