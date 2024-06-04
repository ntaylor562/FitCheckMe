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
import jakarta.persistence.OneToMany;
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

	@ManyToMany
	@JoinTable(
		name = "garment_tag",
		schema = "app",
		joinColumns = @JoinColumn(name = "garment_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
	)
	private Set<Tag> garmentTags;

	@OneToMany(mappedBy="garment")
	private Set<GarmentImage> images;

	public Garment() {

	}

	public Garment(User user, String garmentName, Collection<String> urls, Collection<Tag> tags) {
		this.garmentName = garmentName;
		this.user = user;
		this.urls = urls != null ? new HashSet<>(urls) : new HashSet<>();
		this.garmentTags = tags != null ? new HashSet<>(tags) : new HashSet<>();
		this.images = new HashSet<>();
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

	public Set<GarmentImage> getImages() {
		return this.images;
	}

	public void setName(String name) {
		this.garmentName = name;
	}

	public boolean addTag(Tag tag) {
		return this.garmentTags.add(tag);
	}

	public boolean addTag(Collection<Tag> tags) {
		return this.garmentTags.addAll(tags);
	}

	public boolean removeTag(Integer tagId) {
		return this.garmentTags.removeIf(tag -> tag.getId() == tagId);
	}

	public boolean removeTag(Tag tag) {
		return this.garmentTags.remove(tag);
	}

	public boolean removeTag(Collection<Tag> tagsToBeRemoved) {
		return this.garmentTags.removeAll(tagsToBeRemoved);
	}

	public boolean addURL(String url) {
		return this.urls.add(url);
	}

	public boolean addURL(Collection<String> urls) {
		return this.urls.addAll(urls);
	}

	public boolean removeURL(String url) {
		return this.urls.remove(url);
	}

	public boolean removeURL(Collection<String> urls) {
		return this.urls.removeAll(urls);
	}

	public boolean addImage(GarmentImage image) {
		return this.images.add(image);
	}

	public boolean addImage(Collection<GarmentImage> images) {
		return this.images.addAll(images);
	}

	public boolean removeImage(Integer imageId) {
		return this.images.removeIf(image -> image.getImage().getId() == imageId);
	}

	public boolean removeImage(GarmentImage image) {
		return this.images.remove(image);
	}
}
