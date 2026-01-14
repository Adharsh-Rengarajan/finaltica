package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.AssetType;
import com.finaltica.application.enums.PaymentMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvestmentTransactionRequestDTO {

	@NotNull(message = "Account ID is required")
	private UUID accountId;

	@NotBlank(message = "Asset symbol is required")
	@Size(max = 50, message = "Asset symbol must not exceed 50 characters")
	private String assetSymbol;

	@NotNull(message = "Asset type is required")
	private AssetType assetType;

	@NotNull(message = "Quantity is required")
	@Positive(message = "Quantity must be positive")
	private BigDecimal quantity;

	@NotNull(message = "Price per unit is required")
	@Positive(message = "Price per unit must be positive")
	private BigDecimal pricePerUnit;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;

	@NotNull(message = "Transaction date is required")
	private Instant transactionDate;

	@NotNull(message = "Payment mode is required")
	private PaymentMode paymentMode;
}