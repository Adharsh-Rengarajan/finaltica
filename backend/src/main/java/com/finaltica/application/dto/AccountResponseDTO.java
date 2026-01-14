package com.finaltica.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.AccountType;
import com.finaltica.application.enums.CurrencyCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {

	private UUID id;
	private String name;
	private AccountType type;
	private BigDecimal currentBalance;
	private CurrencyCode currency;
	private Instant createdAt;
	private Instant updatedAt;
}