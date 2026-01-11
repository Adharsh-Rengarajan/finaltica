package com.finaltica.application.entity;

import com.finaltica.application.enums.AssetType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "investment_metadata")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
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
}