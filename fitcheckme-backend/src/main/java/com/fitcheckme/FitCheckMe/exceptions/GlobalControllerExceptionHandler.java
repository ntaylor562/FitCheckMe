package com.fitcheckme.FitCheckMe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fitcheckme.FitCheckMe.DTOs.ExceptionResponseDTO;

import jakarta.persistence.EntityNotFoundException;

//TODO add logging here
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponseDTO> handleException(Exception e) {
		System.out.println(e.getMessage() + e.getClass());
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Unexpected error", "An unexpected error has occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponseDTO> handleAccessDeniedException(AccessDeniedException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Access denied", e.getMessage()), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ExceptionResponseDTO> handleEntityNotFoundException(EntityNotFoundException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Entity not found", e.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionResponseDTO> handleIllegalArgumentException(IllegalArgumentException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Illegal argument", e.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Invalid JSON", "JSON provided does not match what is required"), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ExceptionResponseDTO> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Method not allowed", "Requested method is not allowed"), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ExceptionResponseDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Missing parameter", String.format("Paramater '%s' must be specified", e.getParameterName())), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Invalid parameter type", String.format("Parameter '%s' must have type '%s'", e.getName(), e.getRequiredType())), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return new ResponseEntity<ExceptionResponseDTO>(new ExceptionResponseDTO("Invalid parameter", "Invalid parameter in request body"), HttpStatus.BAD_REQUEST);
	}
}
