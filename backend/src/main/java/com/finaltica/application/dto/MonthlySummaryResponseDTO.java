package com.finaltica.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummaryResponseDTO {

	private int year;
	private int month;
	private BigDecimal totalIncome;
	private BigDecimal totalExpenses;
	private BigDecimal netSavings;
	private int incomeTransactionCount;
	private int expenseTransactionCount;
}