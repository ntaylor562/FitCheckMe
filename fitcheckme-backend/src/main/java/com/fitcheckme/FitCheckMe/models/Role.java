package com.fitcheckme.FitCheckMe.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="Role", schema="app")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Integer roleId;
	
	@NotBlank
	@Column(name = "role_name", nullable = false)
	private String roleName;
	
	public Role() {
		
	}
	
	public Role(String roleName) {
		this.roleName = roleName;
	}
	
	public Integer getId() {
		return this.roleId;
	}
	
	public String getName() {
		return this.roleName;
	}
}
