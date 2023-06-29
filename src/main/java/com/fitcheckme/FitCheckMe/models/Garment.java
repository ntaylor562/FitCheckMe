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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "garment", schema = "app")
public class Garment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "garment_id")
	private Integer garmentId;

	@Column(name = "garment_name")
	private String garmentName;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

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

	public Garment(String garmentName, User user, List<String> urls, List<Tag> tags) {
		this.garmentName = garmentName;
		this.user = user;
		this.urls = urls;
		this.garmentTags = tags;
	}

	public Integer getId() {
		return this.garmentId;
	}

	public String getName() {
		return this.garmentName;
	}

	public User getUser() {
		return this.user;
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

	public void setName(String name) {
		this.garmentName = name;
	}

	public void addOutfit(Outfit outfit) {
		this.outfits.add(outfit);
	}

	public void addOutfit(List<Outfit> outfit) {
		this.outfits.addAll(outfit);
	}

	public void removeOutfit(Outfit outfit) {
		this.outfits.remove(outfit);
	}

	public void removeOutfit(List<Outfit> outfit) {
		this.outfits.removeAll(outfit);
	}

	public void addTag(Tag tag) {
		this.garmentTags.add(tag);
	}

	public void addTag(List<Tag> tags) {
		this.garmentTags.addAll(tags);
	}

	public void removeTag(Tag tag) {
		this.garmentTags.remove(tag);
	}

	public void removeTag(List<Tag> tags) {
		this.garmentTags.removeAll(tags);
	}

	public void addURL(String url) {
		this.urls.add(url);
	}

	public void addURL(List<String> urls) {
		this.urls.addAll(urls);
	}

	public void removeURL(String url) {
		this.urls.remove(url);
	}

	public void removeURL(List<String> urls) {
		//Removing one at a time because unsure whether removeAll will remove duplicates or just one of each
		for(int i = 0; i < urls.size(); ++i) {
			this.urls.remove(urls.get(i));
		}
	}
}
