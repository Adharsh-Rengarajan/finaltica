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
public class CategorySpendingResponseDTO {

	private List<CategorySpending> expenses;
	private List<CategorySpending> income;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CategorySpending {
		private String categoryName;
		private BigDecimal amount;
		private int transactionCount;
	}
}