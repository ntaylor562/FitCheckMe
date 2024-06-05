package com.fitcheckme.FitCheckMe.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.DTOs.FileUploadDTO;
import com.fitcheckme.FitCheckMe.DTOs.files.AWSPresignedURLDTO;
import com.fitcheckme.FitCheckMe.DTOs.files.FileUploadResponseDTO;
import com.fitcheckme.FitCheckMe.auth.CustomUserDetails;
import com.fitcheckme.FitCheckMe.models.ImageFile;
import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.ImageFileRepository;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;
import com.fitcheckme.FitCheckMe.utils.AWSUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class FileService {
	private List<String> allowedExtensions = List.of("jpg", "jpeg", "png");

	@Value("${fitcheckme.aws-s3-bucket-name}")
	private String bucketName;

	private final AWSUtil awsUtil;

	private final UserRepository userRepository;

	private final ImageFileRepository imageFileRepository;

	public FileService(AWSUtil awsUtil, UserRepository userRepository, ImageFileRepository imageFileRepository) {
		this.awsUtil = awsUtil;
		this.userRepository = userRepository;
		this.imageFileRepository = imageFileRepository;
	}

	private boolean validateFileName(String fileName) {
		// Check if the file name is null or empty
		if (fileName == null || fileName.isEmpty()) {
			return false;
		}

		// Check if the file name has an extension
		String[] parts = fileName.split("\\.");
		if (parts.length != 2) {
			return false;
		}

		// Check if the file name has a name and an extension
		if (parts[0].isEmpty() || parts[1].isEmpty()) {
			return false;
		}

		// Check if the file extension is allowed
		if (allowedExtensions.stream().noneMatch(parts[1]::equalsIgnoreCase)) {
			return false;
		}

		// Check if the file name contains any invalid characters
		if (!fileName.matches("^[a-zA-Z0-9_.-]+$")) {
			return false;
		}

		// Check if the file name starts or ends with a dot
		if (fileName.startsWith(".") || fileName.endsWith(".")) {
			return false;
		}

		// Check if the file name has multiple consecutive dots
		if (fileName.contains("..")) {
			return false;
		}

		// Check if the file has a slash in the name
		if (fileName.contains("/") || fileName.contains("\\")) {
			return false;
		}

		return true;
	}

	private String generateFileName(String filename) {
		String[] parts = filename.split("\\.");
		String preExtension = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
		String extension = parts[parts.length - 1];
		String formattedDateTime = LocalDateTime.now().toString().replace(":", "-").replace(".", "-");
		return String.format("%s_%s_%s.%s", UUID.randomUUID().toString(), formattedDateTime, preExtension, extension);
	}

	private AWSPresignedURLDTO createPresignedPutURL(String fileName) throws IllegalArgumentException {
		String presignedURL = awsUtil.createPresignedPutUrl(bucketName, fileName, null);

		return new AWSPresignedURLDTO(fileName, presignedURL);
	}

	private AWSPresignedURLDTO getUploadURL(String fileName) {
		if (!validateFileName(fileName)) {
			throw new IllegalArgumentException("Invalid file name");
		}
		String generatedFileName = generateFileName(fileName);

		return this.createPresignedPutURL(generatedFileName);
	}

	private Set<AWSPresignedURLDTO> getUploadURLs(Iterable<String> fileNames) {
		for (String fileName : fileNames) {
			if (!validateFileName(fileName)) {
				throw new IllegalArgumentException(String.format("Invalid file name: %s", fileName));
			}
		}

		Set<AWSPresignedURLDTO> presignedURLs = new HashSet<>();
		for (String fileName : fileNames) {
			String generatedFileName = generateFileName(fileName);
			AWSPresignedURLDTO presignedURL = this.createPresignedPutURL(generatedFileName);
			presignedURLs.add(presignedURL);
		}

		return presignedURLs;
	}

	@Transactional
	public Set<FileUploadResponseDTO> uploadImages(Collection<FileUploadDTO> files, CustomUserDetails userDetails)
			throws IllegalArgumentException {
		User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new EntityNotFoundException(
				String.format("User not found with ID: %d", userDetails.getUserId())));
		Set<FileUploadResponseDTO> res = new HashSet<>();
		res.addAll(this.getUploadURLs(files.stream().map(FileUploadDTO::fileName).toList()).stream()
				.map(presignedURLDTO -> {
					ImageFile f = imageFileRepository.save(new ImageFile(user, presignedURLDTO.fileName(), LocalDateTime.now()));
					return new FileUploadResponseDTO(user.getId(), f.getId(), presignedURLDTO.fileName(),
						presignedURLDTO.presignedURL());
					})
				.toList());

		return res;
	}

	@Transactional
	public FileUploadResponseDTO uploadImage(FileUploadDTO file, CustomUserDetails userDetails)
			throws IllegalArgumentException {
		User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new EntityNotFoundException(
				String.format("User not found with ID: %d", userDetails.getUserId())));
		AWSPresignedURLDTO presignedURLDTO = this.getUploadURL(file.fileName());
		ImageFile f = imageFileRepository.save(new ImageFile(user, presignedURLDTO.fileName(), LocalDateTime.now()));
		return new FileUploadResponseDTO(user.getId(), f.getId(), presignedURLDTO.fileName(), presignedURLDTO.presignedURL());
	}

	@Transactional
	public void deleteFile(Integer fileId, CustomUserDetails userDetails) {
		User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new EntityNotFoundException(
				String.format("User not found with ID: %d", userDetails.getUserId())));

		if (user.getId() != userDetails.getUserId()) {
			throw new IllegalArgumentException("User does not have permission to delete this file");
		}

		ImageFile imageFile = imageFileRepository.findById(fileId)
				.orElseThrow(() -> new EntityNotFoundException(String.format("File not found with ID: %d", fileId)));
		imageFileRepository.delete(imageFile);
	}
}