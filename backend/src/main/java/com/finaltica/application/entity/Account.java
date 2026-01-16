package com.finaltica.application.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.finaltica.application.enums.AccountType;
import com.finaltica.application.enums.CurrencyCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "name" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 150)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "account_type_enum")
	private AccountType type;

	@Column(name = "current_balance", nullable = false, precision = 18, scale = 2)
	@Builder.Default
	private BigDecimal currentBalance = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "currency_code_enum")
	private CurrencyCode currency;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
	private List<Transaction> transactions;

	@Column(name = "created_at", nullable = false, updatable = false)
	@Builder.Default
	private Instant createdAt = Instant.now();

	@Column(name = "updated_at", nullable = false)
	@Builder.Default
	private Instant updatedAt = Instant.now();
}
