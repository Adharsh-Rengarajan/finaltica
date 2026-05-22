package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.util.List;

import com.finaltica.application.enums.AssetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryResponseDTO {

	private BigDecimal totalInvested;
	private BigDecimal currentValue;
	private BigDecimal totalReturns;
	private BigDecimal returnsPercentage;
	private List<Holding> holdings;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Holding {
		private String assetSymbol;
		private AssetType assetType;
		private BigDecimal totalQuantity;
		private BigDecimal averagePrice;
		private BigDecimal totalInvested;
		private BigDecimal currentPrice;
		private BigDecimal currentValue;
		private BigDecimal returns;
		private BigDecimal returnsPercentage;
	}
}