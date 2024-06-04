package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
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
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "outfit", schema = "app")
public class Outfit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "outfit_id")
	private Integer outfitId;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotBlank
	@Column(name = "outfit_name", nullable = false)
	private String outfitName;
	
	@Column(name = "outfit_desc")
	private String outfitDesc;
	
	@Column(name = "outfit_creation_date", nullable = false)
	private LocalDateTime creationDate;

	@ManyToMany
	@JoinTable(
		name = "outfit_garment",
		schema = "app",
		joinColumns = @JoinColumn(name = "outfit_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "garment_id", nullable = false)
	)
	private Set<Garment> garments;

	@ManyToMany
	@JoinTable(
		name = "outfit_tag",
		schema = "app",
		joinColumns = @JoinColumn(name = "outfit_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
	)
	private Set<Tag> outfitTags;

	@OneToMany(mappedBy = "outfit")
	private Set<OutfitImage> images;

	public Outfit() {

	}

	public Outfit(User user, String outfitName, String outfitDesc, LocalDateTime creationDate, Collection<Garment> garments, Collection<Tag> tags) {
		this.user = user;
		this.outfitName = outfitName;
		this.outfitDesc = outfitDesc;
		this.creationDate = creationDate;
		this.garments = garments != null ? new HashSet<>(garments) : new HashSet<>();
		this.outfitTags = tags != null ? new HashSet<>(tags) : new HashSet<>();
		this.images = new HashSet<>();
	}

	public Integer getId() {
		return this.outfitId;
	}

	public User getUser() {
		return this.user;
	}

	public String getName() {
		return this.outfitName;
	}

	public String getDesc() {
		return this.outfitDesc;
	}

	public LocalDateTime getCreationDate() {
		return this.creationDate;
	}

	public Set<Garment> getGarments() {
		return this.garments;
	}

	public Set<Tag> getTags() {
		return this.outfitTags;
	}

	public Set<OutfitImage> getImages() {
		return this.images;
	}

	public void setName(String name) {
		this.outfitName = name;
	}

	public void setDesc(String desc) {
		this.outfitDesc = desc;
	}

	public boolean addGarment(Garment garment) {
		return this.garments.add(garment);
	}

	public boolean addGarment(Collection<Garment> garments) {
		return this.garments.addAll(garments);
	}

	public boolean removeGarment(Garment garment) {
		return this.garments.remove(garment);
	}

	public boolean removeGarment(Collection<Garment> garments) {
		return this.garments.removeAll(garments);
	}

	public boolean addTag(Tag tag) {
		return this.outfitTags.add(tag);
	}

	public boolean addTag(Collection<Tag> tags) {
		return this.outfitTags.addAll(tags);
	}

	public boolean removeTag(Integer tagId) {
		return this.outfitTags.removeIf(tag -> tag.getId() == tagId);
	}

	public boolean removeTag(Tag tag) {
		return this.outfitTags.remove(tag);
	}

	public boolean removeTag(Collection<Tag> tagsToBeRemoved) {
		return this.outfitTags.removeAll(tagsToBeRemoved);
	}

	public boolean addImage(OutfitImage image) {
		return this.images.add(image);
	}

	public boolean addImage(Collection<OutfitImage> images) {
		return this.images.addAll(images);
	}

	public boolean removeImage(Integer imageId) {
		return this.images.removeIf(image -> image.getImage().getId() == imageId);
	}

	public boolean removeImage(OutfitImage image) {
		return this.images.remove(image);
	}
}
