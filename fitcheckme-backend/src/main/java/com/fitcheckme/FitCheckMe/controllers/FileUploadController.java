package com.fitcheckme.FitCheckMe.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.fitcheckme.FitCheckMe.services.FileService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


//TODO REMOVE THIS TEST CONTROLLER
@RestController
@RequestMapping("/api/file")
public class FileUploadController {
	
	private final FileService fileService;

	public FileUploadController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("")
	public String uploadFile(@RequestBody String fileName) {
		return fileService.createPresignedPutURL(fileName).presignedURL();
	}
	
}
