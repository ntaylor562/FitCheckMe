package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "image_file", schema = "app")
public class ImageFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_file_id")
	private Integer imageFileId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "image_path", nullable = false)
	private String imagePath;

	@Column(name = "image_creation_date", nullable = false)
	private LocalDateTime creationDate;


	public ImageFile() {

	}

	public ImageFile(User user, String imagePath, LocalDateTime creationDate) {
		this.user = user;
		this.imagePath = imagePath;
		this.creationDate = creationDate;
	}

	public Integer getId() {
		return imageFileId;
	}

	public User getUser() {
		return user;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

}