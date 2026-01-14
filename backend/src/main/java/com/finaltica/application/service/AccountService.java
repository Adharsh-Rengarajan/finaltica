package com.finaltica.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finaltica.application.dto.AccountResponseDTO;
import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.dto.CreateAccountRequestDTO;
import com.finaltica.application.dto.UpdateAccountRequestDTO;
import com.finaltica.application.entity.Account;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.AccountType;
import com.finaltica.application.repository.AccountRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAllAccounts(User user) {
		List<Account> accounts = accountRepository.findByUser(user);
		List<AccountResponseDTO> accountDTOs = accounts.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), "Accounts retrieved successfully", accountDTOs));
	}

	public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> getAccountsByType(AccountType type, User user) {
		List<Account> accounts = accountRepository.findByUserAndType(user, type);
		List<AccountResponseDTO> accountDTOs = accounts.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), type + " accounts retrieved successfully", accountDTOs));
	}

	public ResponseEntity<ApiResponse<AccountResponseDTO>> getAccountById(UUID id, User user) {
		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (!account.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You don't have access to this account");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		AccountResponseDTO accountDTO = convertToDTO(account);
		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), "Account retrieved successfully", accountDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(CreateAccountRequestDTO request, User user) {

		if (accountRepository.existsByNameAndUser(request.getName(), user)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("name", "You already have an account named '" + request.getName() + "'");
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Account already exists", errors));
		}

		if (request.getType() == AccountType.CREDIT && request.getInitialBalance().compareTo(java.math.BigDecimal.ZERO) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("initialBalance", "Credit card balance must be zero or negative");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid balance for credit account", errors));
		}

		Account account = Account.builder()
				.name(request.getName())
				.type(request.getType())
				.currency(request.getCurrency())
				.currentBalance(request.getInitialBalance())
				.user(user)
				.build();

		Account saved = accountRepository.save(account);
		AccountResponseDTO accountDTO = convertToDTO(saved);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED.value(), "Account created successfully", accountDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<AccountResponseDTO>> updateAccount(UUID id, UpdateAccountRequestDTO request,
			User user) {

		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (!account.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You can only modify your own accounts");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		if (!account.getName().equals(request.getName())) {
			if (accountRepository.existsByNameAndUser(request.getName(), user)) {
				Map<String, String> errors = new HashMap<>();
				errors.put("name", "You already have an account named '" + request.getName() + "'");
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Account already exists", errors));
			}
		}

		account.setName(request.getName());
		account.setCurrency(request.getCurrency());
		Account updated = accountRepository.save(account);
		AccountResponseDTO accountDTO = convertToDTO(updated);

		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), "Account updated successfully", accountDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<Void>> deleteAccount(UUID id, User user) {
		Account account = accountRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (!account.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You can only delete your own accounts");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		if (accountRepository.hasTransactions(id)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("account", "Cannot delete account with existing transactions");
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Account in use", errors));
		}

		accountRepository.delete(account);

		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), "Account deleted successfully", null));
	}

	private AccountResponseDTO convertToDTO(Account account) {
		return AccountResponseDTO.builder()
				.id(account.getId())
				.name(account.getName())
				.type(account.getType())
				.currentBalance(account.getCurrentBalance())
				.currency(account.getCurrency())
				.createdAt(account.getCreatedAt())
				.updatedAt(account.getUpdatedAt())
				.build();
	}
}