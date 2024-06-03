package com.fitcheckme.FitCheckMe.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ImageFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Integer imageId;
	
	@Column(name = "image_path", nullable = false)
	private String imagePath;

	@Column(name = "image_creation_date", nullable = false)
	private LocalDateTime creationDate;


	public ImageFile() {

	}

	public ImageFile(String imagePath, LocalDateTime creationDate) {
		this.imagePath = imagePath;
		this.creationDate = creationDate;
	}

	public Integer getId() {
		return imageId;
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