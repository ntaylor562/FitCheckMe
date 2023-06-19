package com.fitcheckme.FitCheckMe.DTOs.Outfit;

import java.time.LocalDateTime;
import java.util.List;

import com.fitcheckme.FitCheckMe.DTOs.Garment.GarmentRequestDTO;

public class OutfitRequestDTO {
	private Long outfitId;
	private Long userId;
	private String outfitName;
	private String outfitDesc;
	private LocalDateTime creationDate;
	private List<GarmentRequestDTO>garments;

	public OutfitRequestDTO(Long outfitId, Long userId, String outfitName, String outfitDesc, LocalDateTime creationDate, List<GarmentRequestDTO> garments) {
		this.outfitId = outfitId;
		this.userId = userId;
		this.outfitName = outfitName;
		this.outfitDesc = outfitDesc;
		this.creationDate = creationDate;
		this.garments = garments;
	}

	public Long getOutfitId() {
		return this.outfitId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public String getOutfitName() {
		return this.outfitName;
	}

	public String getOutfitDesc() {
		return this.outfitDesc;
	}

	public LocalDateTime getCreationDate() {
		return this.creationDate;
	}

	public List<GarmentRequestDTO> getGarments() {
		return this.garments;
	}
}
