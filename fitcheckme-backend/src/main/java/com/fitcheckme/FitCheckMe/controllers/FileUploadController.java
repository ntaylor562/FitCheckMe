package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.FileUploadDTO;
import com.fitcheckme.FitCheckMe.DTOs.AWS.AWSPresignedURLDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.services.FileService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/file")
public class FileUploadController {
	
	private final FileService fileService;

	public FileUploadController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("image")
	public ResponseEntity<Set<AWSPresignedURLDTO>> uploadImage(@RequestBody Set<FileUploadDTO> fileNames, @AuthenticationPrincipal CustomUserDetails userDetails) {
		return new ResponseEntity<Set<AWSPresignedURLDTO>>(this.fileService.uploadImages(fileNames, userDetails), HttpStatus.OK);
	}
	
}
