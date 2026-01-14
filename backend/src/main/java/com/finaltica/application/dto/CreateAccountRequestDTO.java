package com.finaltica.application.dto;

import java.math.BigDecimal;

import com.finaltica.application.enums.AccountType;
import com.finaltica.application.enums.CurrencyCode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequestDTO {

	@NotBlank(message = "Account name is required")
	@Size(min = 2, max = 150, message = "Account name must be between 2 and 150 characters")
	private String name;

	@NotNull(message = "Account type is required")
	private AccountType type;

	@NotNull(message = "Currency is required")
	private CurrencyCode currency;

	@NotNull(message = "Initial balance is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Initial balance must be greater than 0")
	private BigDecimal initialBalance;
}