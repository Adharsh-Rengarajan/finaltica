package com.finaltica.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.dto.LoginRequestDTO;
import com.finaltica.application.dto.LoginResponseDTO;
import com.finaltica.application.dto.SignupRequestDTO;
import com.finaltica.application.dto.SignupResponseDTO;
import com.finaltica.application.entity.User;
import com.finaltica.application.repository.UserRepository;
import com.finaltica.application.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;

	private SignupRequestDTO validSignupRequest;
	private LoginRequestDTO validLoginRequest;
	private User mockUser;
	private String mockToken;

	@BeforeEach
	void setUp() {
		// Create valid signup request
		validSignupRequest = new SignupRequestDTO();
		validSignupRequest.setFirstName("John");
		validSignupRequest.setLastName("Doe");
		validSignupRequest.setEmail("john.doe@example.com");
		validSignupRequest.setPassword("Password123!");
		validSignupRequest.setConfirmPassword("Password123!");

		// Create valid login request
		validLoginRequest = new LoginRequestDTO();
		validLoginRequest.setEmail("john.doe@example.com");
		validLoginRequest.setPassword("Password123!");

		// Create mock user
		mockUser = new User();
		mockUser.setId(UUID.randomUUID());
		mockUser.setFirstName("John");
		mockUser.setLastName("Doe");
		mockUser.setEmail("john.doe@example.com");
		mockUser.setPasswordHash("hashedPassword");

		// Mock JWT token
		mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mockToken";
	}

	// ==================== SIGNUP TESTS (Your existing tests) ====================

	@Test
	void signup_WithValidData_ShouldReturnSuccessResponse() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// Act
		ResponseEntity<ApiResponse<SignupResponseDTO>> response = authService.signup(validSignupRequest);

		// Assert
		assertNotNull(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertTrue(response.getBody().isSuccess());
		assertEquals("User registered successfully", response.getBody().getMessage());
		assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatusCode());

		SignupResponseDTO responseData = response.getBody().getData();
		assertNotNull(responseData);
		assertEquals("John", responseData.getFirstName());
		assertEquals("Doe", responseData.getLastName());
		assertEquals("john.doe@example.com", responseData.getEmail());
		assertNotNull(responseData.getId());

		// Verify interactions
		verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
		verify(passwordEncoder, times(1)).encode("Password123!");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void signup_WithExistingEmail_ShouldReturnBadRequest() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		// Act
		ResponseEntity<ApiResponse<SignupResponseDTO>> response = authService.signup(validSignupRequest);

		// Assert
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertFalse(response.getBody().isSuccess());
		assertEquals("Email already exists", response.getBody().getMessage());
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatusCode());
		assertNull(response.getBody().getData());
		assertNotNull(response.getBody().getErrors());
		assertEquals("Email is already in use", response.getBody().getErrors().get("email"));

		// Verify interactions
		verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
		verify(passwordEncoder, never()).encode(anyString());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void signup_ShouldHashPassword() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// Act
		authService.signup(validSignupRequest);

		// Assert
		verify(passwordEncoder, times(1)).encode("Password123!");
	}

	@Test
	void signup_ShouldSaveUserWithCorrectData() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User savedUser = invocation.getArgument(0);
			assertEquals("John", savedUser.getFirstName());
			assertEquals("Doe", savedUser.getLastName());
			assertEquals("john.doe@example.com", savedUser.getEmail());
			assertEquals("hashedPassword", savedUser.getPasswordHash());
			return mockUser;
		});

		// Act
		authService.signup(validSignupRequest);

		// Assert
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void signup_ShouldNotExposePasswordInResponse() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// Act
		ResponseEntity<ApiResponse<SignupResponseDTO>> response = authService.signup(validSignupRequest);

		// Assert
		SignupResponseDTO responseData = response.getBody().getData();
		assertNotNull(responseData);
		// SignupResponseDTO should not have password or passwordHash fields
		assertEquals("John", responseData.getFirstName());
		assertEquals("Doe", responseData.getLastName());
		assertEquals("john.doe@example.com", responseData.getEmail());
	}

	// ==================== LOGIN TESTS (New tests) ====================

	@Test
	void login_WithValidCredentials_ShouldReturnSuccessResponse() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().isSuccess());
		assertEquals("Login successful", response.getBody().getMessage());
		assertEquals(HttpStatus.OK.value(), response.getBody().getStatusCode());

		LoginResponseDTO responseData = response.getBody().getData();
		assertNotNull(responseData);
		assertEquals(mockToken, responseData.getToken());
		assertNotNull(responseData.getUser());
		assertEquals("John", responseData.getUser().getFirstName());
		assertEquals("Doe", responseData.getUser().getLastName());
		assertEquals("john.doe@example.com", responseData.getUser().getEmail());
		assertEquals(mockUser.getId(), responseData.getUser().getId());

		// Verify interactions
		verify(userRepository, times(1)).findByEmail("john.doe@example.com");
		verify(passwordEncoder, times(1)).matches("Password123!", "hashedPassword");
		verify(jwtUtil, times(1)).generateToken(mockUser);
	}

	@Test
	void login_WithNonExistentEmail_ShouldReturnUnauthorized() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		assertNotNull(response);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertFalse(response.getBody().isSuccess());
		assertEquals("Invalid credentials", response.getBody().getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatusCode());
		assertNull(response.getBody().getData());
		assertNotNull(response.getBody().getErrors());
		assertEquals("Invalid email or password", response.getBody().getErrors().get("credentials"));

		// Verify interactions
		verify(userRepository, times(1)).findByEmail("john.doe@example.com");
		verify(passwordEncoder, never()).matches(anyString(), anyString());
		verify(jwtUtil, never()).generateToken(any(User.class));
	}

	@Test
	void login_WithIncorrectPassword_ShouldReturnUnauthorized() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		assertNotNull(response);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertFalse(response.getBody().isSuccess());
		assertEquals("Invalid credentials", response.getBody().getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().getStatusCode());
		assertNull(response.getBody().getData());
		assertNotNull(response.getBody().getErrors());
		assertEquals("Invalid email or password", response.getBody().getErrors().get("credentials"));

		// Verify interactions
		verify(userRepository, times(1)).findByEmail("john.doe@example.com");
		verify(passwordEncoder, times(1)).matches("Password123!", "hashedPassword");
		verify(jwtUtil, never()).generateToken(any(User.class));
	}

	@Test
	void login_ShouldGenerateJwtToken() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		verify(jwtUtil, times(1)).generateToken(mockUser);
		assertNotNull(response.getBody().getData().getToken());
		assertEquals(mockToken, response.getBody().getData().getToken());
	}

	@Test
	void login_ShouldNotExposePasswordInResponse() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		LoginResponseDTO responseData = response.getBody().getData();
		assertNotNull(responseData);
		assertNotNull(responseData.getUser());
		// LoginResponseDTO.UserInfo should not have password or passwordHash fields
		assertEquals("John", responseData.getUser().getFirstName());
		assertEquals("Doe", responseData.getUser().getLastName());
		assertEquals("john.doe@example.com", responseData.getUser().getEmail());
	}

	@Test
	void login_ShouldReturnCorrectUserInfo() {
		// Arrange
		UUID expectedId = UUID.randomUUID();
		mockUser.setId(expectedId);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		LoginResponseDTO.UserInfo userInfo = response.getBody().getData().getUser();
		assertNotNull(userInfo);
		assertEquals(expectedId, userInfo.getId());
		assertEquals("john.doe@example.com", userInfo.getEmail());
		assertEquals("John", userInfo.getFirstName());
		assertEquals("Doe", userInfo.getLastName());
	}

	@Test
	void login_WithValidCredentials_ShouldHaveNullErrors() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		ResponseEntity<ApiResponse<LoginResponseDTO>> response = authService.login(validLoginRequest);

		// Assert
		assertNull(response.getBody().getErrors());
	}

	@Test
	void login_ShouldVerifyPasswordWithCorrectHashedPassword() {
		// Arrange
		String specificHashedPassword = "specificHashedPassword123";
		mockUser.setPasswordHash(specificHashedPassword);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(mockToken);

		// Act
		authService.login(validLoginRequest);

		// Assert
		verify(passwordEncoder, times(1)).matches("Password123!", specificHashedPassword);
	}
}