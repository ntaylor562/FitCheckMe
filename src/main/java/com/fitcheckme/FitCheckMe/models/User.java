package com.fitcheckme.FitCheckMe.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;


//TODO implement auth stuff
//TODO implement checks for max length stuff (do this for all TEXT columns)
@Entity
@Table(name = "user", schema = "community")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@NotBlank
	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "bio")
	private String bio;

	@OneToMany(mappedBy = "user")
	private List<Outfit> outfits;

	@OneToMany(mappedBy = "follower")
	private List<Following> followers;

	@OneToMany(mappedBy = "followee")
	private List<Following> following;

	public User() {

	}

	public User(String username, String bio) {
		this.username = username;
		this.bio = bio;
	}

	public Long getId() {
		return this.userId;
	}

	public String getUsername() {
		return this.username;
	}

	public String getBio() {
		return this.bio;
	}

	public List<Outfit> getOutfits() {
		return this.outfits;
	}

	public List<Following> getFollowers() {
		return this.followers;
	}

	public List<Following> getFollowing() {
		return this.following;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
	
}
