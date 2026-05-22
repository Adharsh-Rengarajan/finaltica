package com.finaltica.application.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
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

import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.dto.CreateInvestmentTransactionRequestDTO;
import com.finaltica.application.dto.CreateTransactionRequestDTO;
import com.finaltica.application.dto.CreateTransferRequestDTO;
import com.finaltica.application.dto.InvestmentMetadataResponseDTO;
import com.finaltica.application.dto.InvestmentTransactionResponseDTO;
import com.finaltica.application.dto.TransactionResponseDTO;
import com.finaltica.application.entity.Account;
import com.finaltica.application.entity.Category;
import com.finaltica.application.entity.InvestmentMetadata;
import com.finaltica.application.entity.Transaction;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.AccountType;
import com.finaltica.application.enums.TransactionType;
import com.finaltica.application.repository.AccountRepository;
import com.finaltica.application.repository.CategoryRepository;
import com.finaltica.application.repository.InvestmentMetadataRepository;
import com.finaltica.application.repository.TransactionRepository;

@Service
public class TransactionService {

	private static final BigDecimal MAX_QUANTITY = new BigDecimal("1000000000"); // 1 billion units
	private static final BigDecimal MAX_PRICE_PER_UNIT = new BigDecimal("10000000"); // $10M per unit
	private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000000"); // $1B per txn

	private static final Duration FUTURE_DATE_TOLERANCE = Duration.ofDays(1);

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private InvestmentMetadataRepository investmentMetadataRepository;

	public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getAllTransactions(User user) {
		List<Transaction> transactions = transactionRepository
				.findByAccount_User_IdOrderByTransactionDateDesc(user.getId());
		List<TransactionResponseDTO> transactionDTOs = transactions.stream().map(this::convertToDTO)
				.collect(Collectors.toList());

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Transactions retrieved successfully", transactionDTOs));
	}

	public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getFilteredTransactions(UUID accountId,
			UUID categoryId, TransactionType type, Instant startDate, Instant endDate, User user) {

		if (accountId != null) {
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account == null) {
				Map<String, String> errors = new HashMap<>();
				errors.put("account", "Account not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found", errors));
			}
			if (!account.getUser().getId().equals(user.getId())) {
				Map<String, String> errors = new HashMap<>();
				errors.put("authorization", "You don't have access to this account");
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
			}
		}

		// Same check for categoryId.
		if (categoryId != null) {
			Category category = categoryRepository.findById(categoryId).orElse(null);
			if (category == null) {
				Map<String, String> errors = new HashMap<>();
				errors.put("category", "Category not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Category not found", errors));
			}
			if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
				Map<String, String> errors = new HashMap<>();
				errors.put("authorization", "You don't have access to this category");
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
			}
		}

		List<Transaction> transactions;

		if (startDate != null && endDate != null) {
			if (startDate.isAfter(endDate)) {
				Map<String, String> errors = new HashMap<>();
				errors.put("dateRange", "startDate must be before or equal to endDate");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid date range", errors));
			}
			transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);
		} else if (categoryId != null) {
			transactions = transactionRepository.findByUserIdAndCategoryId(user.getId(), categoryId);
		} else if (type != null) {
			transactions = transactionRepository.findByUserIdAndType(user.getId(), type);
		} else if (accountId != null) {
			transactions = transactionRepository.findByAccountId(accountId);
		} else {
			transactions = transactionRepository.findByAccount_User_IdOrderByTransactionDateDesc(user.getId());
		}

		List<TransactionResponseDTO> transactionDTOs = transactions.stream().map(this::convertToDTO)
				.collect(Collectors.toList());

		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(),
				"Filtered transactions retrieved successfully", transactionDTOs));
	}

	public ResponseEntity<ApiResponse<TransactionResponseDTO>> getTransactionById(UUID id, User user) {
		Transaction transaction = transactionRepository.findByIdAndAccount_User_Id(id, user.getId()).orElse(null);
		if (transaction == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("transaction", "Transaction not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Transaction not found", errors));
		}

		TransactionResponseDTO transactionDTO = convertToDTO(transaction);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction retrieved successfully", transactionDTO));
	}

	public ResponseEntity<ApiResponse<List<InvestmentTransactionResponseDTO>>> getInvestmentTransactions(UUID accountId,
			User user) {

		List<InvestmentMetadata> metadataList;
		if (accountId != null) {
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account == null) {
				Map<String, String> errors = new HashMap<>();
				errors.put("account", "Account not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found", errors));
			}
			if (!account.getUser().getId().equals(user.getId())) {
				Map<String, String> errors = new HashMap<>();
				errors.put("authorization", "You don't have access to this account");
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
			}
			metadataList = investmentMetadataRepository.findByAccountId(accountId);
		} else {
			metadataList = investmentMetadataRepository.findByUserId(user.getId());
		}

		List<InvestmentTransactionResponseDTO> result = metadataList.stream().map(meta -> {
			return InvestmentTransactionResponseDTO.builder().transaction(convertToDTO(meta.getTransaction()))
					.investmentMetadata(convertInvestmentMetadataToDTO(meta)).build();
		}).collect(Collectors.toList());

		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), "Investment transactions retrieved successfully", result));
	}

	@Transactional
	public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(CreateTransactionRequestDTO request,
			User user) {

		Account account = accountRepository.findById(request.getAccountId()).orElse(null);
		if (account == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("account", "Account not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found", errors));
		}

		if (!account.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You don't have access to this account");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		if (request.getType() == TransactionType.TRANSFER) {
			Map<String, String> errors = new HashMap<>();
			errors.put("type", "Use the transfer endpoint for transfer transactions");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid transaction type", errors));
		}

		if ((request.getType() == TransactionType.INCOME && request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
				|| (request.getType() == TransactionType.EXPENSE
						&& request.getAmount().compareTo(BigDecimal.ZERO) >= 0)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount", "Income must be positive, expense must be negative");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid amount", errors));
		}

		if (request.getAmount().abs().compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount", "Amount exceeds maximum allowed (" + MAX_TRANSACTION_AMOUNT.toPlainString() + ")");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Amount too large", errors));
		}

		ResponseEntity<ApiResponse<TransactionResponseDTO>> dateErr = validateTransactionDate(
				request.getTransactionDate());
		if (dateErr != null) {
			return dateErr;
		}

		if (request.getType() == TransactionType.EXPENSE && account.getType() != AccountType.CREDIT) {
			BigDecimal projectedBalance = account.getCurrentBalance().add(request.getAmount());
			if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
				Map<String, String> errors = new HashMap<>();
				errors.put("amount",
						String.format("Insufficient balance. Available: %s %s, required: %s %s",
								account.getCurrentBalance().toPlainString(), account.getCurrency(),
								request.getAmount().abs().toPlainString(), account.getCurrency()));
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Insufficient balance", errors));
			}
		}

		Category category = null;
		if (request.getCategoryId() != null) {
			category = categoryRepository.findById(request.getCategoryId()).orElse(null);
			if (category == null) {
				Map<String, String> errors = new HashMap<>();
				errors.put("category", "Category not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Category not found", errors));
			}
			if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
				Map<String, String> errors = new HashMap<>();
				errors.put("category", "You don't have access to this category");
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
			}
		}

		Transaction transaction = Transaction.builder().account(account).category(category).amount(request.getAmount())
				.type(request.getType()).description(request.getDescription())
				.transactionDate(request.getTransactionDate()).paymentMode(request.getPaymentMode()).build();

		account.setCurrentBalance(account.getCurrentBalance().add(request.getAmount()));
		accountRepository.save(account);

		Transaction saved = transactionRepository.save(transaction);
		TransactionResponseDTO transactionDTO = convertToDTO(saved);

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.success(HttpStatus.CREATED.value(), "Transaction created successfully", transactionDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<Map<String, TransactionResponseDTO>>> createTransfer(
			CreateTransferRequestDTO request, User user) {

		Account fromAccount = accountRepository.findById(request.getFromAccountId()).orElse(null);
		if (fromAccount == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("fromAccountId", "From account not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "From account not found", errors));
		}

		Account toAccount = accountRepository.findById(request.getToAccountId()).orElse(null);
		if (toAccount == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("toAccountId", "To account not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "To account not found", errors));
		}

		if (!fromAccount.getUser().getId().equals(user.getId()) || !toAccount.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You don't have access to one or both accounts");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		if (request.getFromAccountId().equals(request.getToAccountId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("accounts", "Cannot transfer to the same account");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid transfer", errors));
		}

		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount", "Transfer amount must be greater than zero");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid amount", errors));
		}

		if (request.getAmount().compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount", "Amount exceeds maximum allowed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Amount too large", errors));
		}

		if (fromAccount.getType() == AccountType.CREDIT) {
			Map<String, String> errors = new HashMap<>();
			errors.put("fromAccountId",
					"Cannot transfer out of a credit card account. Pay it down with a regular transfer to it instead.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid source account", errors));
		}

		// Cross-currency transfers without an FX rate would silently mix currencies.
		if (fromAccount.getCurrency() != toAccount.getCurrency()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("accounts", String.format("Cannot transfer between accounts in different currencies (%s -> %s)",
					fromAccount.getCurrency(), toAccount.getCurrency()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Currency mismatch", errors));
		}

		// Date sanity check.
		ResponseEntity<ApiResponse<Map<String, TransactionResponseDTO>>> dateErr = validateTransactionDate(
				request.getTransactionDate());
		if (dateErr != null) {
			return dateErr;
		}

		BigDecimal projectedBalance = fromAccount.getCurrentBalance().subtract(request.getAmount());
		if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount",
					String.format("Insufficient balance in '%s'. Available: %s %s, required: %s %s",
							fromAccount.getName(), fromAccount.getCurrentBalance().toPlainString(),
							fromAccount.getCurrency(), request.getAmount().toPlainString(), fromAccount.getCurrency()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Insufficient balance", errors));
		}

		fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(request.getAmount()));
		toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(request.getAmount()));
		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);

		Transaction debitTransaction = new Transaction();
		debitTransaction.setAccount(fromAccount);
		debitTransaction.setAmount(request.getAmount().negate());
		debitTransaction.setType(TransactionType.TRANSFER);
		debitTransaction.setDescription(request.getDescription());
		debitTransaction.setTransactionDate(request.getTransactionDate());
		debitTransaction.setPaymentMode(request.getPaymentMode());

		Transaction creditTransaction = new Transaction();
		creditTransaction.setAccount(toAccount);
		creditTransaction.setAmount(request.getAmount());
		creditTransaction.setType(TransactionType.TRANSFER);
		creditTransaction.setDescription(request.getDescription());
		creditTransaction.setTransactionDate(request.getTransactionDate());
		creditTransaction.setPaymentMode(request.getPaymentMode());

		debitTransaction.setRelatedTransaction(creditTransaction);
		creditTransaction.setRelatedTransaction(debitTransaction);

		List<Transaction> savedTransactions = transactionRepository
				.saveAll(List.of(creditTransaction, debitTransaction));

		Map<String, TransactionResponseDTO> transferResult = new HashMap<>();
		transferResult.put("debit", convertToDTO(savedTransactions.get(1)));
		transferResult.put("credit", convertToDTO(savedTransactions.get(0)));

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ApiResponse.success(HttpStatus.CREATED.value(), "Transfer completed successfully", transferResult));
	}

	@Transactional
	public ResponseEntity<ApiResponse<InvestmentTransactionResponseDTO>> createInvestmentTransaction(
			CreateInvestmentTransactionRequestDTO request, User user) {

		Account account = accountRepository.findById(request.getAccountId()).orElse(null);
		if (account == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("account", "Account not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Account not found", errors));
		}

		if (!account.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You don't have access to this account");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		if (account.getType() != AccountType.INVESTMENT) {
			Map<String, String> errors = new HashMap<>();
			errors.put("account", "Investment transactions can only be created in INVESTMENT accounts");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid account type", errors));
		}

		if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("quantity", "Quantity must be greater than zero");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid quantity", errors));
		}
		// Issue #12: cap quantity and price-per-unit so garbage requests can't
		// land enormous synthetic positions.
		if (request.getQuantity().compareTo(MAX_QUANTITY) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("quantity", "Quantity exceeds maximum allowed (" + MAX_QUANTITY.toPlainString() + ")");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Quantity too large", errors));
		}
		if (request.getPricePerUnit() == null || request.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("pricePerUnit", "Price per unit must be greater than zero");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid price", errors));
		}
		if (request.getPricePerUnit().compareTo(MAX_PRICE_PER_UNIT) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("pricePerUnit",
					"Price per unit exceeds maximum allowed (" + MAX_PRICE_PER_UNIT.toPlainString() + ")");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Price too large", errors));
		}
		if (request.getAssetSymbol() == null || request.getAssetSymbol().trim().isEmpty()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("assetSymbol", "Asset symbol is required");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid asset symbol", errors));
		}

		// Date sanity check.
		ResponseEntity<ApiResponse<InvestmentTransactionResponseDTO>> dateErr = validateTransactionDate(
				request.getTransactionDate());
		if (dateErr != null) {
			return dateErr;
		}

		BigDecimal totalCost = request.getPricePerUnit().multiply(request.getQuantity());
		if (totalCost.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount", "Total cost exceeds maximum allowed transaction amount");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Total cost too large", errors));
		}
		BigDecimal signedAmount = totalCost.negate();

		BigDecimal projectedBalance = account.getCurrentBalance().add(signedAmount);
		if (projectedBalance.compareTo(BigDecimal.ZERO) < 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("amount",
					String.format(
							"Insufficient balance in '%s'. Available: %s %s, required: %s %s. Transfer funds in first.",
							account.getName(), account.getCurrentBalance().toPlainString(), account.getCurrency(),
							totalCost.toPlainString(), account.getCurrency()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Insufficient balance", errors));
		}

		Transaction transaction = Transaction.builder().account(account).amount(signedAmount)
				.type(TransactionType.EXPENSE).description(request.getDescription())
				.transactionDate(request.getTransactionDate()).paymentMode(request.getPaymentMode()).build();

		Transaction savedTransaction = transactionRepository.save(transaction);

		InvestmentMetadata metadata = InvestmentMetadata.builder().transaction(savedTransaction)
				.assetSymbol(request.getAssetSymbol().trim().toUpperCase()).assetType(request.getAssetType())
				.quantity(request.getQuantity()).pricePerUnit(request.getPricePerUnit()).build();

		InvestmentMetadata savedMetadata = investmentMetadataRepository.save(metadata);

		account.setCurrentBalance(account.getCurrentBalance().add(signedAmount));
		accountRepository.save(account);

		InvestmentTransactionResponseDTO responseDTO = InvestmentTransactionResponseDTO.builder()
				.transaction(convertToDTO(savedTransaction))
				.investmentMetadata(convertInvestmentMetadataToDTO(savedMetadata)).build();

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),
				"Investment transaction created successfully", responseDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<Void>> deleteTransaction(UUID id, User user) {
		Transaction transaction = transactionRepository.findByIdAndAccount_User_Id(id, user.getId()).orElse(null);
		if (transaction == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("transaction", "Transaction not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Transaction not found", errors));
		}

		if (transaction.getType() == TransactionType.TRANSFER) {
			Map<String, String> errors = new HashMap<>();
			errors.put("transaction", "Cannot delete transfer transactions individually");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid operation", errors));
		}

		investmentMetadataRepository.findByTransactionId(transaction.getId())
				.ifPresent(investmentMetadataRepository::delete);

		Account account = transaction.getAccount();
		account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
		accountRepository.save(account);

		transactionRepository.delete(transaction);

		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction deleted successfully", null));
	}

	/**
	 * Reject transactions dated more than {@link #FUTURE_DATE_TOLERANCE} in the
	 * future. Returns null if the date is OK, or a 400 ResponseEntity if it isn't.
	 * Generic so it can be returned from any of the create-* methods.
	 */
	private <T> ResponseEntity<ApiResponse<T>> validateTransactionDate(Instant transactionDate) {
		if (transactionDate == null) {
			return null;
		}
		Instant cutoff = Instant.now().plus(FUTURE_DATE_TOLERANCE);
		if (transactionDate.isAfter(cutoff)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("transactionDate", "Transaction date cannot be in the future");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid transaction date", errors));
		}
		return null;
	}

	private TransactionResponseDTO convertToDTO(Transaction transaction) {
		return TransactionResponseDTO.builder().id(transaction.getId()).accountId(transaction.getAccount().getId())
				.accountName(transaction.getAccount().getName())
				.categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
				.categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
				.relatedTransactionId(
						transaction.getRelatedTransaction() != null ? transaction.getRelatedTransaction().getId()
								: null)
				.amount(transaction.getAmount()).type(transaction.getType()).description(transaction.getDescription())
				.transactionDate(transaction.getTransactionDate()).paymentMode(transaction.getPaymentMode())
				.createdAt(transaction.getCreatedAt()).updatedAt(transaction.getUpdatedAt()).build();
	}

	private InvestmentMetadataResponseDTO convertInvestmentMetadataToDTO(InvestmentMetadata metadata) {
		return InvestmentMetadataResponseDTO.builder().transactionId(metadata.getTransactionId())
				.assetSymbol(metadata.getAssetSymbol()).assetType(metadata.getAssetType())
				.quantity(metadata.getQuantity()).pricePerUnit(metadata.getPricePerUnit())
				.totalAmount(metadata.getPricePerUnit().multiply(metadata.getQuantity()))
				.createdAt(metadata.getCreatedAt()).updatedAt(metadata.getUpdatedAt()).build();
	}
}