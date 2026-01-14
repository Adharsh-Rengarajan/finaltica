package com.finaltica.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finaltica.application.entity.InvestmentMetadata;
import com.finaltica.application.enums.AssetType;

@Repository
public interface InvestmentMetadataRepository extends JpaRepository<InvestmentMetadata, UUID> {

	Optional<InvestmentMetadata> findByTransactionId(UUID transactionId);

	@Query("SELECT im FROM InvestmentMetadata im WHERE im.transaction.account.user.id = :userId")
	List<InvestmentMetadata> findByUserId(@Param("userId") UUID userId);

	@Query("SELECT im FROM InvestmentMetadata im WHERE im.transaction.account.user.id = :userId AND im.assetType = :assetType")
	List<InvestmentMetadata> findByUserIdAndAssetType(@Param("userId") UUID userId,
			@Param("assetType") AssetType assetType);

	@Query("SELECT im FROM InvestmentMetadata im WHERE im.transaction.account.id = :accountId")
	List<InvestmentMetadata> findByAccountId(@Param("accountId") UUID accountId);
}