package com.finaltica.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.finaltica.application.dto.PortfolioSummaryResponseDTO;
import com.finaltica.application.entity.Account;
import com.finaltica.application.entity.InvestmentMetadata;
import com.finaltica.application.entity.Transaction;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.AccountType;
import com.finaltica.application.enums.AssetType;
import com.finaltica.application.enums.TransactionType;
import com.finaltica.application.repository.AccountRepository;
import com.finaltica.application.repository.InvestmentMetadataRepository;
import com.finaltica.application.repository.TransactionRepository;

@Service
public class AnalyticsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private InvestmentMetadataRepository investmentMetadataRepository;

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

	public ResponseEntity<ApiResponse<PortfolioSummaryResponseDTO>> getPortfolio(User user) {
		List<InvestmentMetadata> allMetadata = investmentMetadataRepository.findByUserId(user.getId());

		Map<String, List<InvestmentMetadata>> bySymbol = allMetadata.stream()
				.collect(Collectors.groupingBy(InvestmentMetadata::getAssetSymbol));

		List<PortfolioSummaryResponseDTO.Holding> holdings = new ArrayList<>();
		BigDecimal portfolioInvested = BigDecimal.ZERO;
		BigDecimal portfolioCurrentValue = BigDecimal.ZERO;

		for (Map.Entry<String, List<InvestmentMetadata>> entry : bySymbol.entrySet()) {
			List<InvestmentMetadata> purchases = entry.getValue();

			BigDecimal totalQuantity = BigDecimal.ZERO;
			BigDecimal totalInvested = BigDecimal.ZERO;
			AssetType assetType = purchases.get(0).getAssetType();

			for (InvestmentMetadata m : purchases) {
				BigDecimal lotCost = m.getQuantity().multiply(m.getPricePerUnit());
				totalQuantity = totalQuantity.add(m.getQuantity());
				totalInvested = totalInvested.add(lotCost);
			}

			BigDecimal avgPrice = totalQuantity.compareTo(BigDecimal.ZERO) > 0
					? totalInvested.divide(totalQuantity, 4, RoundingMode.HALF_UP)
					: BigDecimal.ZERO;

			BigDecimal currentPrice = avgPrice;
			BigDecimal currentValue = totalQuantity.multiply(currentPrice).setScale(2, RoundingMode.HALF_UP);
			BigDecimal returns = currentValue.subtract(totalInvested).setScale(2, RoundingMode.HALF_UP);
			BigDecimal returnsPct = totalInvested.compareTo(BigDecimal.ZERO) > 0
					? returns.multiply(BigDecimal.valueOf(100)).divide(totalInvested, 2, RoundingMode.HALF_UP)
					: BigDecimal.ZERO;

			holdings.add(PortfolioSummaryResponseDTO.Holding.builder().assetSymbol(entry.getKey()).assetType(assetType)
					.totalQuantity(totalQuantity).averagePrice(avgPrice)
					.totalInvested(totalInvested.setScale(2, RoundingMode.HALF_UP)).currentPrice(currentPrice)
					.currentValue(currentValue).returns(returns).returnsPercentage(returnsPct).build());

			portfolioInvested = portfolioInvested.add(totalInvested);
			portfolioCurrentValue = portfolioCurrentValue.add(currentValue);
		}

		BigDecimal totalReturns = portfolioCurrentValue.subtract(portfolioInvested).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalReturnsPct = portfolioInvested.compareTo(BigDecimal.ZERO) > 0
				? totalReturns.multiply(BigDecimal.valueOf(100)).divide(portfolioInvested, 2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;

		PortfolioSummaryResponseDTO response = PortfolioSummaryResponseDTO.builder()
				.totalInvested(portfolioInvested.setScale(2, RoundingMode.HALF_UP))
				.currentValue(portfolioCurrentValue.setScale(2, RoundingMode.HALF_UP)).totalReturns(totalReturns)
				.returnsPercentage(totalReturnsPct).holdings(holdings).build();

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Portfolio summary retrieved successfully", response));
	}
}