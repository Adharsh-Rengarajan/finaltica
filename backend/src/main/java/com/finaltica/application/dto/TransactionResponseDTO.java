package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.PaymentMode;
import com.finaltica.application.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {

	private UUID id;
	private UUID accountId;
	private String accountName;
	private UUID categoryId;
	private String categoryName;
	private UUID relatedTransactionId;
	private BigDecimal amount;
	private TransactionType type;
	private String description;
	private Instant transactionDate;
	private PaymentMode paymentMode;
	private Instant createdAt;
	private Instant updatedAt;
}