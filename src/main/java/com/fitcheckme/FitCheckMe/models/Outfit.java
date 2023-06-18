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

	public Outfit() {

	}

	public Outfit(User user, String outfitName, String outfitDesc, LocalDateTime creationDate) {
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
}
