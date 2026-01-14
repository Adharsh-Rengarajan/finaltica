package com.finaltica.application.service;

import java.math.BigDecimal;
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

		List<Transaction> transactions;

		if (startDate != null && endDate != null) {
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
		Transaction transaction = transactionRepository.findByIdAndAccount_User_Id(id, user.getId())
				.orElseThrow(() -> new RuntimeException("Transaction not found"));

		TransactionResponseDTO transactionDTO = convertToDTO(transaction);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction retrieved successfully", transactionDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(CreateTransactionRequestDTO request,
			User user) {

		Account account = accountRepository.findById(request.getAccountId())
				.orElseThrow(() -> new RuntimeException("Account not found"));

		if (!account.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You don't have access to this account");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		if (request.getType() == TransactionType.TRANSFER) {
			Map<String, String> errors = new HashMap<>();
			errors.put("type", "Use transfer endpoint for transfer transactions");
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

		Category category = null;
		if (request.getCategoryId() != null) {
			category = categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found"));

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

		Account fromAccount = accountRepository.findById(request.getFromAccountId())
				.orElseThrow(() -> new RuntimeException("From account not found"));

		Account toAccount = accountRepository.findById(request.getToAccountId())
				.orElseThrow(() -> new RuntimeException("To account not found"));

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

		Account account = accountRepository.findById(request.getAccountId())
				.orElseThrow(() -> new RuntimeException("Account not found"));

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

		BigDecimal totalAmount = request.getPricePerUnit().multiply(request.getQuantity()).negate();

		Transaction transaction = Transaction.builder().account(account).amount(totalAmount)
				.type(TransactionType.EXPENSE).description(request.getDescription())
				.transactionDate(request.getTransactionDate()).paymentMode(request.getPaymentMode()).build();

		Transaction savedTransaction = transactionRepository.save(transaction);

		InvestmentMetadata metadata = InvestmentMetadata.builder().transaction(savedTransaction)
				.assetSymbol(request.getAssetSymbol()).assetType(request.getAssetType()).quantity(request.getQuantity())
				.pricePerUnit(request.getPricePerUnit()).build();

		InvestmentMetadata savedMetadata = investmentMetadataRepository.save(metadata);

		account.setCurrentBalance(account.getCurrentBalance().add(totalAmount));
		accountRepository.save(account);

		InvestmentTransactionResponseDTO responseDTO = InvestmentTransactionResponseDTO.builder()
				.transaction(convertToDTO(savedTransaction))
				.investmentMetadata(convertInvestmentMetadataToDTO(savedMetadata)).build();

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(),
				"Investment transaction created successfully", responseDTO));
	}

	@Transactional
	public ResponseEntity<ApiResponse<Void>> deleteTransaction(UUID id, User user) {
		Transaction transaction = transactionRepository.findByIdAndAccount_User_Id(id, user.getId())
				.orElseThrow(() -> new RuntimeException("Transaction not found"));

		if (transaction.getType() == TransactionType.TRANSFER) {
			Map<String, String> errors = new HashMap<>();
			errors.put("transaction", "Cannot delete transfer transactions individually");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid operation", errors));
		}

		Account account = transaction.getAccount();
		account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
		accountRepository.save(account);

		transactionRepository.delete(transaction);

		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Transaction deleted successfully", null));
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