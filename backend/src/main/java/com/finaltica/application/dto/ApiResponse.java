package com.finaltica.application.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private int statusCode;
	private boolean success;
	private String message;
	private T data;
	private Map<String, String> errors;

	// Success response
	public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
		return new ApiResponse<>(statusCode, true, message, data, null);
	}

	// Error response
	public static <T> ApiResponse<T> error(int statusCode, String message, Map<String, String> errors) {
		return new ApiResponse<>(statusCode, false, message, null, errors);
	}
}