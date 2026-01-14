package com.finaltica.application.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finaltica.application.entity.Account;
import com.finaltica.application.entity.Transaction;
import com.finaltica.application.enums.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

	List<Transaction> findByAccountOrderByTransactionDateDesc(Account account);

	List<Transaction> findByAccount_User_IdOrderByTransactionDateDesc(UUID userId);

	List<Transaction> findByAccountAndTypeOrderByTransactionDateDesc(Account account, TransactionType type);

	@Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
	List<Transaction> findByUserIdAndDateRange(@Param("userId") UUID userId, @Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate);

	Optional<Transaction> findByIdAndAccount_User_Id(UUID id, UUID userId);

	@Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.category.id = :categoryId ORDER BY t.transactionDate DESC")
	List<Transaction> findByUserIdAndCategoryId(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);

	@Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.type = :type ORDER BY t.transactionDate DESC")
	List<Transaction> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") TransactionType type);

	@Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.transactionDate DESC")
	List<Transaction> findByAccountId(@Param("accountId") UUID accountId);
}