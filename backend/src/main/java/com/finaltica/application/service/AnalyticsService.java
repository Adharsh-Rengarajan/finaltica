package com.finaltica.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.dto.CategorySpendingResponseDTO;
import com.finaltica.application.dto.MonthlySummaryResponseDTO;
import com.finaltica.application.dto.NetWorthResponseDTO;
import com.finaltica.application.entity.Account;
import com.finaltica.application.entity.Transaction;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.AccountType;
import com.finaltica.application.enums.TransactionType;
import com.finaltica.application.repository.AccountRepository;
import com.finaltica.application.repository.TransactionRepository;

@Service
public class AnalyticsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public ResponseEntity<ApiResponse<NetWorthResponseDTO>> getNetWorth(User user) {
		List<Account> accounts = accountRepository.findByUser(user);

		BigDecimal totalAssets = BigDecimal.ZERO;
		BigDecimal totalLiabilities = BigDecimal.ZERO;
		List<NetWorthResponseDTO.AccountSummary> accountSummaries = new ArrayList<>();

		for (Account account : accounts) {
			if (account.getType() == AccountType.CREDIT) {
				totalLiabilities = totalLiabilities.add(account.getCurrentBalance().abs());
			} else {
				totalAssets = totalAssets.add(account.getCurrentBalance());
			}

			accountSummaries.add(NetWorthResponseDTO.AccountSummary.builder().accountName(account.getName())
					.accountType(account.getType().toString()).balance(account.getCurrentBalance()).build());
		}

		BigDecimal netWorth = totalAssets.subtract(totalLiabilities);

		NetWorthResponseDTO response = NetWorthResponseDTO.builder().totalAssets(totalAssets)
				.totalLiabilities(totalLiabilities).netWorth(netWorth).accounts(accountSummaries).build();

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Net worth calculated successfully", response));
	}

	public ResponseEntity<ApiResponse<MonthlySummaryResponseDTO>> getMonthlySummary(User user, int year, int month) {
		YearMonth yearMonth = YearMonth.of(year, month);
		Instant startDate = yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
		Instant endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

		List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate,
				endDate);

		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpenses = BigDecimal.ZERO;
		int incomeCount = 0;
		int expenseCount = 0;

		for (Transaction t : transactions) {
			if (t.getType() == TransactionType.INCOME) {
				totalIncome = totalIncome.add(t.getAmount());
				incomeCount++;
			} else if (t.getType() == TransactionType.EXPENSE) {
				totalExpenses = totalExpenses.add(t.getAmount().abs());
				expenseCount++;
			}
		}

		BigDecimal netSavings = totalIncome.subtract(totalExpenses);

		MonthlySummaryResponseDTO response = MonthlySummaryResponseDTO.builder().year(year).month(month)
				.totalIncome(totalIncome).totalExpenses(totalExpenses).netSavings(netSavings)
				.incomeTransactionCount(incomeCount).expenseTransactionCount(expenseCount).build();

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Monthly summary retrieved successfully", response));
	}

	public ResponseEntity<ApiResponse<CategorySpendingResponseDTO>> getCategorySpending(User user, Instant startDate,
			Instant endDate) {

		List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(user.getId(), startDate,
				endDate);

		Map<String, BigDecimal> expenseMap = new HashMap<>();
		Map<String, Integer> expenseCountMap = new HashMap<>();
		Map<String, BigDecimal> incomeMap = new HashMap<>();
		Map<String, Integer> incomeCountMap = new HashMap<>();

		for (Transaction t : transactions) {
			if (t.getCategory() != null) {
				String categoryName = t.getCategory().getName();

				if (t.getType() == TransactionType.EXPENSE) {
					expenseMap.put(categoryName,
							expenseMap.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount().abs()));
					expenseCountMap.put(categoryName, expenseCountMap.getOrDefault(categoryName, 0) + 1);
				} else if (t.getType() == TransactionType.INCOME) {
					incomeMap.put(categoryName,
							incomeMap.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount()));
					incomeCountMap.put(categoryName, incomeCountMap.getOrDefault(categoryName, 0) + 1);
				}
			}
		}

		List<CategorySpendingResponseDTO.CategorySpending> expenses = expenseMap.entrySet().stream()
				.map(e -> CategorySpendingResponseDTO.CategorySpending.builder().categoryName(e.getKey())
						.amount(e.getValue()).transactionCount(expenseCountMap.get(e.getKey())).build())
				.collect(Collectors.toList());

		List<CategorySpendingResponseDTO.CategorySpending> income = incomeMap.entrySet().stream()
				.map(e -> CategorySpendingResponseDTO.CategorySpending.builder().categoryName(e.getKey())
						.amount(e.getValue()).transactionCount(incomeCountMap.get(e.getKey())).build())
				.collect(Collectors.toList());

		CategorySpendingResponseDTO response = CategorySpendingResponseDTO.builder().expenses(expenses).income(income)
				.build();

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Category spending retrieved successfully", response));
	}
}