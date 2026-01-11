package com.finaltica.application.validation;

import com.finaltica.application.dto.SignupRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignupRequestDTO> {

	@Override
	public boolean isValid(SignupRequestDTO dto, ConstraintValidatorContext context) {
		if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
			return true; // Let @NotBlank handle null checks
		}
		return dto.getPassword().equals(dto.getConfirmPassword());
	}
}