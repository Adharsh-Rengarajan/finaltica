package com.finaltica.application.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.finaltica.application.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getAllErrors().forEach((error) -> {
			if (error instanceof FieldError) {
				String fieldName = ((FieldError) error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			} else if (error instanceof ObjectError) {
				String objectName = error.getObjectName();
				String errorMessage = error.getDefaultMessage();
				errors.put(objectName, errorMessage);
			}
		});

		return ResponseEntity.badRequest()
				.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(
				HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage(), null));
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("category", ex.getMessage());

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), errors));
	}

	@ExceptionHandler(DuplicateCategoryException.class)
	public ResponseEntity<ApiResponse<Object>> handleDuplicateCategory(DuplicateCategoryException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("name", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Category already exists", errors));
	}

	@ExceptionHandler(CannotModifyGlobalCategoryException.class)
	public ResponseEntity<ApiResponse<Object>> handleCannotModifyGlobal(CannotModifyGlobalCategoryException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("category", ex.getMessage());

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), ex.getMessage(), errors));
	}

	@ExceptionHandler(CategoryInUseException.class)
	public ResponseEntity<ApiResponse<Object>> handleCategoryInUse(CategoryInUseException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("category", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage(), errors));
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ApiResponse<Object>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("authorization", ex.getMessage());

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
	}
}