package com.finaltica.application.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.AssetType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investment_metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentMetadata {

	@Id
	@Column(name = "transaction_id")
	private UUID transactionId;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId // This binds the ID to the Transaction entity's ID
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;

	@Column(name = "asset_symbol", nullable = false, length = 50)
	private String assetSymbol;

	@Enumerated(EnumType.STRING)
	@Column(name = "asset_type", nullable = false)
	private AssetType assetType;

	@Column(nullable = false, precision = 20, scale = 8)
	private BigDecimal quantity;

	@Column(name = "price_per_unit", nullable = false, precision = 18, scale = 4)
	private BigDecimal pricePerUnit;

	@Column(name = "created_at", nullable = false, updatable = false)
	@Builder.Default
	private Instant createdAt = Instant.now();

	@Column(name = "updated_at", nullable = false)
	@Builder.Default
	private Instant updatedAt = Instant.now();
}