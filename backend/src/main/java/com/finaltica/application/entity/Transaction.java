package com.finaltica.application.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.PaymentMode;
import com.finaltica.application.enums.TransactionType;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "related_transaction_id")
	private Transaction relatedTransaction;

	@Column(nullable = false, precision = 18, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TransactionType type;

	@Column(length = 500)
	private String description;

	@Column(name = "transaction_date", nullable = false)
	private Instant transactionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_mode", nullable = false, length = 20)
	private PaymentMode paymentMode;

	@Column(name = "created_at", nullable = false, updatable = false)
	@Builder.Default
	private Instant createdAt = Instant.now();

	@Column(name = "updated_at", nullable = false)
	@Builder.Default
	private Instant updatedAt = Instant.now();

	@OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
	private InvestmentMetadata investmentMetadata;
}