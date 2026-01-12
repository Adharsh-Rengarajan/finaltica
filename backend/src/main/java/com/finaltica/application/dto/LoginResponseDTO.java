package com.finaltica.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
	private String token;
	private UserInfo user;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		private UUID id;
		private String email;
		private String firstName;
		private String lastName;
	}
}