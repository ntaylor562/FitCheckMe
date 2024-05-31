package com.fitcheckme.FitCheckMe.models;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

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

	@ManyToMany
	@JoinTable(
		name = "user_role",
		schema = "app",
		joinColumns = @JoinColumn(name = "user_id", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
	)
	private Set<Role> roles;

	@OneToMany(mappedBy = "follower")
	private Set<Following> followers;

	@OneToMany(mappedBy = "followee")
	private Set<Following> following;

	public User() {

	}

	public User(String username, String email, String password, String bio, Collection<Role> roles) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.bio = bio;
		this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
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

	public Set<Role> getRoles() {
		return this.roles;
	}

	public Set<Following> getFollowers() {
		return this.followers;
	}

	public Set<Following> getFollowing() {
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

	public void setRoles(Collection<Role> roles) {
		this.roles = new HashSet<>(roles);
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}

	public void addRole(Collection<Role> roles) {
		this.roles.addAll(roles);
	}

	public void removeRole(Role role) {
		this.roles.remove(role);
	}

	public void removeRole(Collection<Role> roles) {
		this.roles.removeAll(roles);
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
