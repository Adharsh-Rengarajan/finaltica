package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetWorthResponseDTO {

	private BigDecimal totalAssets;
	private BigDecimal totalLiabilities;
	private BigDecimal netWorth;
	private List<AccountSummary> accounts;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class AccountSummary {
		private String accountName;
		private String accountType;
		private BigDecimal balance;
	}
}