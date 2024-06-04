package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.DTOs.FileUploadDTO;
import com.fitcheckme.FitCheckMe.DTOs.files.FileUploadResponseDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.services.FileService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/file")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("images")
	public ResponseEntity<Set<FileUploadResponseDTO>> uploadImage(@RequestBody Set<FileUploadDTO> fileNames,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		return new ResponseEntity<Set<FileUploadResponseDTO>>(this.fileService.uploadImages(fileNames, userDetails),
				HttpStatus.OK);
	}

	@PostMapping("image")
	public ResponseEntity<FileUploadResponseDTO> uploadImage(@RequestBody FileUploadDTO fileName,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		return new ResponseEntity<FileUploadResponseDTO>(this.fileService.uploadImage(fileName, userDetails),
				HttpStatus.OK);
	}

	@DeleteMapping("")
	@ResponseStatus(HttpStatus.OK)
	public void deleteFile(@RequestParam Integer fileId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		this.fileService.deleteFile(fileId, userDetails);
	}

}
