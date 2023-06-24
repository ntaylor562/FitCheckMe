package com.fitcheckme.FitCheckMe.DTOs.Outfit;

public record OutfitUpdateRequestDTO (
	Integer outfitId,
	String outfitName,
	String outfitDesc
	//TODO Think out how to update the garments
) {}
