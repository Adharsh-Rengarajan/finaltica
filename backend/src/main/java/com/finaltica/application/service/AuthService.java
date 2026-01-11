package com.finaltica.application.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.dto.SignupRequestDTO;
import com.finaltica.application.dto.SignupResponseDTO;
import com.finaltica.application.entity.User;
import com.finaltica.application.repository.UserRepository;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public ResponseEntity<ApiResponse<SignupResponseDTO>> signup(SignupRequestDTO signupRequest) {
		// Input validation already handled by @Valid

		// Business logic: Check if email already exists
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("email", "Email is already in use");

			return ResponseEntity.badRequest()
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Email already exists", errors));
		}

		// Create user
		User user = new User();
		user.setFirstName(signupRequest.getFirstName());
		user.setLastName(signupRequest.getLastName());
		user.setEmail(signupRequest.getEmail());
		user.setPasswordHash(passwordEncoder.encode(signupRequest.getPassword()));

		User savedUser = userRepository.save(user);

		// Create response
		SignupResponseDTO responseData = new SignupResponseDTO(savedUser.getFirstName(), savedUser.getLastName(),
				savedUser.getEmail(), savedUser.getId());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED.value(), "User registered successfully", responseData));
	}
}