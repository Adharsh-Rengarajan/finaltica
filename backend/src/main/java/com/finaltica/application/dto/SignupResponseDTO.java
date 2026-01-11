package com.finaltica.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SignupResponseDTO {
	private String firstName;
	private String lastName;
	private String email;
	private UUID id;
}
