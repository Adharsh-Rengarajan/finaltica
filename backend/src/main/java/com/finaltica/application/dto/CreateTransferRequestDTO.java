package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.PaymentMode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferRequestDTO {

	@NotNull(message = "From account ID is required")
	private UUID fromAccountId;

	@NotNull(message = "To account ID is required")
	private UUID toAccountId;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	private BigDecimal amount;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;

	@NotNull(message = "Transaction date is required")
	private Instant transactionDate;

	@NotNull(message = "Payment mode is required")
	private PaymentMode paymentMode;
}