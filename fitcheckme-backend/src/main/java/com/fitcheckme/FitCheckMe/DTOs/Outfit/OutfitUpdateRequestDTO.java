package com.fitcheckme.FitCheckMe.DTOs.Outfit;

public record OutfitUpdateRequestDTO (
	Integer outfitId,
	String outfitName,
	String outfitDesc
) {}
