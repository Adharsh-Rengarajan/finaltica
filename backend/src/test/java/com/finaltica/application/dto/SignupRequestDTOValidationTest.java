package com.finaltica.application.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class SignupRequestDTOValidationTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void validSignupRequest_ShouldPassValidation() {
		// Arrange
		SignupRequestDTO dto = new SignupRequestDTO();
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setEmail("john.doe@example.com");
		dto.setPassword("Password123!");
		dto.setConfirmPassword("Password123!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
	}

	// ========== First Name Tests ==========

	@Test
	void firstName_WhenNull_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName(null);

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
	}

	@Test
	void firstName_WhenEmpty_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName("");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")
				&& v.getMessage().contains("First name is required")));
	}

	@Test
	void firstName_WhenBlank_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName("   ");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
	}

	@Test
	void firstName_WhenTooShort_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName("J");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")
				&& v.getMessage().contains("between 2 and 50")));
	}

	@Test
	void firstName_WhenTooLong_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName("A".repeat(51));

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
	}

	@Test
	void firstName_WithMinLength_ShouldPassValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName("Jo");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertTrue(violations.isEmpty());
	}

	@Test
	void firstName_WithMaxLength_ShouldPassValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setFirstName("A".repeat(50));

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertTrue(violations.isEmpty());
	}

	// ========== Last Name Tests ==========

	@Test
	void lastName_WhenNull_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setLastName(null);

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
	}

	@Test
	void lastName_WhenEmpty_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setLastName("");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
	}

	@Test
	void lastName_WhenTooShort_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setLastName("D");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
	}

	@Test
	void lastName_WhenTooLong_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setLastName("D".repeat(51));

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
	}

	// ========== Email Tests ==========

	@Test
	void email_WhenNull_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setEmail(null);

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	void email_WhenEmpty_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setEmail("");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
	}

	@Test
	void email_WhenInvalidFormat_ShouldFailValidation() {
		// Arrange & Act & Assert
		String[] invalidEmails = { "invalid", // No @ symbol
				"invalid@", // No domain
				"@invalid.com", // No local part
				"invalid@.com", // Domain starts with dot
				"invalid..email@example.com", // Consecutive dots
				"invalid @example.com" // Space in email
		};

		for (String invalidEmail : invalidEmails) {
			SignupRequestDTO dto = createValidDTO();
			dto.setEmail(invalidEmail);
			Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

			assertFalse(violations.isEmpty(), "Email '" + invalidEmail + "' should fail validation");
			assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
					"Email '" + invalidEmail + "' should have email violation");
		}
	}

	@Test
	void email_WithValidFormats_ShouldPassValidation() {
		// Arrange & Act & Assert
		String[] validEmails = { "user@example.com", "user.name@example.com", "user+tag@example.co.uk",
				"user_name@example-domain.com", "123@example.com" };

		for (String validEmail : validEmails) {
			SignupRequestDTO dto = createValidDTO();
			dto.setEmail(validEmail);
			Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

			assertTrue(violations.isEmpty(), "Email '" + validEmail + "' should pass validation");
		}
	}

	// ========== Password Tests ==========

	@Test
	void password_WhenNull_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword(null);

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}

	@Test
	void password_WhenEmpty_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}

	@Test
	void password_WhenTooShort_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("Pass1!");
		dto.setConfirmPassword("Pass1!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
	}

	@Test
	void password_WithoutUppercase_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("password123!");
		dto.setConfirmPassword("password123!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(
				v -> v.getPropertyPath().toString().equals("password") && v.getMessage().contains("uppercase")));
	}

	@Test
	void password_WithoutLowercase_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("PASSWORD123!");
		dto.setConfirmPassword("PASSWORD123!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(
				v -> v.getPropertyPath().toString().equals("password") && v.getMessage().contains("lowercase")));
	}

	@Test
	void password_WithoutNumber_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("Password!");
		dto.setConfirmPassword("Password!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(v -> v.getPropertyPath().toString().equals("password") && v.getMessage().contains("number")));
	}

	@Test
	void password_WithoutSpecialCharacter_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("Password123");
		dto.setConfirmPassword("Password123");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")
				&& v.getMessage().contains("special character")));
	}

	@Test
	void password_WithAllRequirements_ShouldPassValidation() {
		// Arrange & Act & Assert
		String[] validPasswords = { "Password123!", "P@ssw0rd", "Abcd123$", "Test123!@#", "MyP@ssw0rd" };

		for (String validPassword : validPasswords) {
			SignupRequestDTO dto = createValidDTO();
			dto.setPassword(validPassword);
			dto.setConfirmPassword(validPassword);
			Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

			assertTrue(violations.isEmpty(), "Password '" + validPassword + "' should pass validation");
		}
	}

	// ========== Confirm Password Tests ==========

	@Test
	void confirmPassword_WhenNull_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setConfirmPassword(null);

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("confirmPassword")));
	}

	@Test
	void confirmPassword_WhenEmpty_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setConfirmPassword("");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
	}

	// ========== Password Match Tests ==========

	@Test
	void passwords_WhenDoNotMatch_ShouldFailValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("Password123!");
		dto.setConfirmPassword("DifferentPassword123!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must match")));
	}

	@Test
	void passwords_WhenMatch_ShouldPassValidation() {
		// Arrange
		SignupRequestDTO dto = createValidDTO();
		dto.setPassword("Password123!");
		dto.setConfirmPassword("Password123!");

		// Act
		Set<ConstraintViolation<SignupRequestDTO>> violations = validator.validate(dto);

		// Assert
		assertTrue(violations.isEmpty());
	}

	// ========== Helper Method ==========

	private SignupRequestDTO createValidDTO() {
		SignupRequestDTO dto = new SignupRequestDTO();
		dto.setFirstName("John");
		dto.setLastName("Doe");
		dto.setEmail("john.doe@example.com");
		dto.setPassword("Password123!");
		dto.setConfirmPassword("Password123!");
		return dto;
	}
}