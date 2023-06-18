package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "outfit", schema = "app")
public class Outfit {
	@Transient
	@Value("${fitcheckme.max-outfit-desc-length}")
	private int maxDescLength;

	@Transient
	@Value("${fitcheckme.max-outfit-name-length}")
	private int maxNameLength;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "outfit_id")
	private Long outfitId;
	
	@NotBlank
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotBlank
	@Column(name = "outfit_name")
	private String outfitName;
	
	@Column(name = "outfit_desc")
	private String outfitDesc;
	
	@NotBlank
	@Column(name = "outfit_creation_date")
	private LocalDateTime creationDate;

	@ManyToMany
	@JoinTable(
		name = "outfit_garment",
		joinColumns = @JoinColumn(name = "outfit_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "garment_id", nullable = false)
	)
	private List<Garment> garments;

	@ManyToMany
	@JoinTable(
		name = "outfit_tag",
		joinColumns = @JoinColumn(name = "outfit_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
	)
	private List<Tag> outfitTags;

	public Outfit() {

	}

	public Outfit(User user, String outfitName, String outfitDesc, LocalDateTime creationDate) {
		if(outfitName.length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		if(outfitDesc.length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
		}

		this.user = user;
		this.outfitName = outfitName;
		this.outfitDesc = outfitDesc;
		this.creationDate = creationDate;
	}

	public Long getId() {
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
		if(name.length() > maxNameLength) {
			throw new IllegalArgumentException(String.format("Outfit name must be at most %d characters", maxNameLength));
		}
		this.outfitName = name;
	}

	public void setDesc(String desc) {
		if(desc.length() > maxDescLength) {
			throw new IllegalArgumentException(String.format("Outfit description must be at most %d characters", maxDescLength));
		}
		this.outfitDesc = desc;
	}
}
