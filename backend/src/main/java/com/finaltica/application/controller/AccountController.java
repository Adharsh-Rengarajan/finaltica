package com.finaltica.application.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finaltica.application.dto.CreateAccountRequestDTO;
import com.finaltica.application.dto.UpdateAccountRequestDTO;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.AccountType;
import com.finaltica.application.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping
	public ResponseEntity<?> getAccounts(
			@RequestParam(required = false) AccountType type,
			@AuthenticationPrincipal User user) {

		if (type != null) {
			return accountService.getAccountsByType(type, user);
		}
		return accountService.getAllAccounts(user);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getAccountById(
			@PathVariable UUID id,
			@AuthenticationPrincipal User user) {

		return accountService.getAccountById(id, user);
	}

	@PostMapping
	public ResponseEntity<?> createAccount(
			@Valid @RequestBody CreateAccountRequestDTO createAccountRequestDTO,
			@AuthenticationPrincipal User user) {

		return accountService.createAccount(createAccountRequestDTO, user);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateAccount(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateAccountRequestDTO updateAccountRequestDTO,
			@AuthenticationPrincipal User user) {

		return accountService.updateAccount(id, updateAccountRequestDTO, user);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAccount(
			@PathVariable UUID id,
			@AuthenticationPrincipal User user) {

		return accountService.deleteAccount(id, user);
	}
}