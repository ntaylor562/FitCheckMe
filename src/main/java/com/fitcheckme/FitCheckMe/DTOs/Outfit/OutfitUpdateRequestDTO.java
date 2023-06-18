package com.fitcheckme.FitCheckMe.DTOs.Outfit;

public record OutfitUpdateRequestDTO (
	Long outfitId,
	String outfitName,
	String outfitDesc
	//TODO Think out how to update the garments
) {}
