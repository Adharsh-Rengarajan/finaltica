package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.PaymentMode;
import com.finaltica.application.enums.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequestDTO {

	@NotNull(message = "Account ID is required")
	private UUID accountId;

	private UUID categoryId;

	@NotNull(message = "Amount is required")
	private BigDecimal amount;

	@NotNull(message = "Transaction type is required")
	private TransactionType type;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;

	@NotNull(message = "Transaction date is required")
	private Instant transactionDate;

	@NotNull(message = "Payment mode is required")
	private PaymentMode paymentMode;
}