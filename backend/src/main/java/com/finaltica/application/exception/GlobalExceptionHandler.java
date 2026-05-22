package com.finaltica.application.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.finaltica.application.dto.ApiResponse;

import jakarta.persistence.OptimisticLockException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Object>> handleMissingParam(MissingServletRequestParameterException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put(ex.getParameterName(), "Required parameter '" + ex.getParameterName() + "' is missing");
		return ResponseEntity.badRequest()
				.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Missing parameter", errors));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		Map<String, String> errors = new HashMap<>();
		String required = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "expected type";
		errors.put(ex.getName(), "'" + ex.getName() + "' must be a valid " + required);
		return ResponseEntity.badRequest()
				.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid parameter", errors));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Object>> handleUnreadable(HttpMessageNotReadableException ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("body", "Request body is malformed or contains invalid values");
		return ResponseEntity.badRequest()
				.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Malformed request body", errors));
	}

	@ExceptionHandler({ OptimisticLockException.class, ObjectOptimisticLockingFailureException.class })
	public ResponseEntity<ApiResponse<Object>> handleOptimisticLock(Exception ex) {
		Map<String, String> errors = new HashMap<>();
		errors.put("conflict", "This account was modified by another request. Please refresh and try again.");
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Conflict — please retry", errors));
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

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex) {
		String msg = ex.getMessage() != null ? ex.getMessage() : "";
		if (msg.toLowerCase().contains("not found")) {
			Map<String, String> errors = new HashMap<>();
			errors.put("resource", msg);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), msg, errors));
		}
		log.error("Unhandled runtime exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
		// Log the actual stack trace server-side but don't echo it to the client.
		log.error("Unhandled exception", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", null));
	}
}