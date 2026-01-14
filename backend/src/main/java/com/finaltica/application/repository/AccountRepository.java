package com.finaltica.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finaltica.application.entity.Account;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.AccountType;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

	List<Account> findByUser(User user);

	List<Account> findByUserAndType(User user, AccountType type);

	boolean existsByNameAndUser(String name, User user);

	Optional<Account> findByNameAndUser(String name, User user);

	@Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transaction t WHERE t.account.id = :accountId")
	boolean hasTransactions(@Param("accountId") UUID accountId);
}