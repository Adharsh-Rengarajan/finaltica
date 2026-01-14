package com.finaltica.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentTransactionResponseDTO {

	private TransactionResponseDTO transaction;
	private InvestmentMetadataResponseDTO investmentMetadata;
}