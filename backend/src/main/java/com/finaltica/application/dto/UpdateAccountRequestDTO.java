package com.finaltica.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequestDTO {

	@NotBlank(message = "Account name is required")
	@Size(min = 2, max = 150, message = "Account name must be between 2 and 150 characters")
	private String name;
}