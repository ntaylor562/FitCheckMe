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
	private Integer userId;

	@NotBlank
	@Column(name = "username", unique = true, nullable = false)
	private String username;

	@NotBlank
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@NotBlank
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "bio")
	private String bio;

	@OneToMany(mappedBy = "follower")
	private List<Following> followers;

	@OneToMany(mappedBy = "followee")
	private List<Following> following;

	public User() {

	}

	public User(String username, String email, String password, String bio) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.bio = bio;
	}

	public Integer getId() {
		return this.userId;
	}

	public String getUsername() {
		return this.username;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

	public String getBio() {
		return this.bio;
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

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void addFollower(User user) {
		Following following = new Following(user, this);
		this.followers.add(following);
	}

	public void addFollowee(User user) {
		Following following = new Following(this, user);
		this.following.add(following);
	}

}
