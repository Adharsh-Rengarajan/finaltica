package com.finaltica.application.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finaltica.application.dto.CreateInvestmentTransactionRequestDTO;
import com.finaltica.application.dto.CreateTransactionRequestDTO;
import com.finaltica.application.dto.CreateTransferRequestDTO;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.TransactionType;
import com.finaltica.application.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@GetMapping
	public ResponseEntity<?> getAllTransactions(@RequestParam(required = false) UUID accountId,
			@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) TransactionType type,
			@RequestParam(required = false) Instant startDate, @RequestParam(required = false) Instant endDate,
			@AuthenticationPrincipal User user) {

		if (accountId != null || categoryId != null || type != null || startDate != null || endDate != null) {
			return transactionService.getFilteredTransactions(accountId, categoryId, type, startDate, endDate, user);
		}
		return transactionService.getAllTransactions(user);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTransactionById(@PathVariable UUID id, @AuthenticationPrincipal User user) {

		return transactionService.getTransactionById(id, user);
	}

	@PostMapping
	public ResponseEntity<?> createTransaction(
			@Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO,
			@AuthenticationPrincipal User user) {

		return transactionService.createTransaction(createTransactionRequestDTO, user);
	}

	@PostMapping("/transfer")
	public ResponseEntity<?> createTransfer(@Valid @RequestBody CreateTransferRequestDTO createTransferRequestDTO,
			@AuthenticationPrincipal User user) {

		return transactionService.createTransfer(createTransferRequestDTO, user);
	}

	@PostMapping("/investment")
	public ResponseEntity<?> createInvestmentTransaction(
			@Valid @RequestBody CreateInvestmentTransactionRequestDTO createInvestmentTransactionRequestDTO,
			@AuthenticationPrincipal User user) {

		return transactionService.createInvestmentTransaction(createInvestmentTransactionRequestDTO, user);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTransaction(@PathVariable UUID id, @AuthenticationPrincipal User user) {

		return transactionService.deleteTransaction(id, user);
	}
}