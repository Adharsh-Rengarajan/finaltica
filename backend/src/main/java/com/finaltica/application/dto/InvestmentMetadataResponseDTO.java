package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.AssetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentMetadataResponseDTO {

	private UUID transactionId;
	private String assetSymbol;
	private AssetType assetType;
	private BigDecimal quantity;
	private BigDecimal pricePerUnit;
	private BigDecimal totalAmount;
	private Instant createdAt;
	private Instant updatedAt;
}