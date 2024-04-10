package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
	private List<Garment> garments;

	@ManyToMany
	@JoinTable(
		name = "outfit_tag",
		schema = "app",
		joinColumns = @JoinColumn(name = "outfit_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
	)
	private List<Tag> outfitTags;

	public Outfit() {

	}

	public Outfit(User user, String outfitName, String outfitDesc, LocalDateTime creationDate, List<Garment> garments, List<Tag> tags) {
		this.user = user;
		this.outfitName = outfitName;
		this.outfitDesc = outfitDesc;
		this.creationDate = creationDate;
		this.garments = garments;
		this.outfitTags = tags;
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

	public List<Garment> getGarments() {
		return this.garments;
	}

	public List<Tag> getTags() {
		return this.outfitTags;
	}

	public void setName(String name) {
		this.outfitName = name;
	}

	public void setDesc(String desc) {
		this.outfitDesc = desc;
	}

	public void addGarment(Garment garment) {
		this.garments.add(garment);
	}

	public void addGarment(List<Garment> garments) {
		this.garments.addAll(garments);
	}

	public void removeGarment(Garment garment) {
		this.garments.remove(garment);
	}

	public void removeGarment(List<Garment> garments) {
		this.garments.removeAll(garments);
	}

	public void addTag(Tag tag) {
		this.outfitTags.add(tag);
	}

	public void addTag(List<Tag> tags) {
		this.outfitTags.addAll(tags);
	}

	public void removeTag(Integer tagId) {
		this.outfitTags.removeIf(tag -> tag.getId() == tagId);
	}

	public void removeTag(Tag tag) {
		this.outfitTags.remove(tag);
	}

	public void removeTag(List<Tag> tagsToBeRemoved) {
		for (Tag tag : tagsToBeRemoved) {
			this.outfitTags.remove(tag);
		}
	}
}
