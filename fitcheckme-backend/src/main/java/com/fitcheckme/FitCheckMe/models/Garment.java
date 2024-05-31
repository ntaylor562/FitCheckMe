package com.fitcheckme.FitCheckMe.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

	@ElementCollection
	@CollectionTable(
		name = "garment_url",
		schema = "app",
		joinColumns = @JoinColumn(name = "garment_id")
	)
	@Column(name = "garment_url")
	private Set<String> urls;

	@ManyToMany(mappedBy="garments")
	private Set<Outfit> outfits;

	@ManyToMany
	@JoinTable(
		name = "garment_tag",
		schema = "app",
		joinColumns = @JoinColumn(name = "garment_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
	)
	private Set<Tag> garmentTags;

	public Garment() {

	}

	public Garment(User user, String garmentName, Collection<String> urls, Collection<Tag> tags) {
		this.garmentName = garmentName;
		this.user = user;
		this.urls = urls != null ? new HashSet<>(urls) : new HashSet<>();
		this.garmentTags = tags != null ? new HashSet<>(tags) : new HashSet<>();
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

	public Set<String> getURLs() {
		return this.urls;
	}

	public Set<Tag> getTags() {
		return this.garmentTags;
	}

	public void setName(String name) {
		this.garmentName = name;
	}

	public void addOutfit(Outfit outfit) {
		this.outfits.add(outfit);
	}

	public void addOutfit(Collection<Outfit> outfit) {
		this.outfits.addAll(outfit);
	}

	public void removeOutfit(Integer outfitId) {
		this.outfits.removeIf(outfit -> outfit.getId() == outfitId);
	}

	public void removeOutfit(Collection<Outfit> outfit) {
		this.outfits.removeAll(outfit);
	}

	public void removeOutfit(Outfit outfit) {
		this.outfits.remove(outfit);
	}

	public void addTag(Tag tag) {
		this.garmentTags.add(tag);
	}

	public void addTag(Collection<Tag> tags) {
		this.garmentTags.addAll(tags);
	}

	public void removeTag(Integer tagId) {
		this.garmentTags.removeIf(tag -> tag.getId() == tagId);
	}

	public void removeTag(Tag tag) {
		this.garmentTags.remove(tag);
	}

	public void removeTag(Collection<Tag> tagsToBeRemoved) {
		this.garmentTags.removeAll(tagsToBeRemoved);
	}

	public void addURL(String url) {
		this.urls.add(url);
	}

	public void addURL(Collection<String> urls) {
		this.urls.addAll(urls);
	}

	public void removeURL(String url) {
		this.urls.remove(url);
	}

	public void removeURL(Collection<String> urls) {
		this.urls.removeAll(urls);
	}
}
