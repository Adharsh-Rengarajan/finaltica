package com.finaltica.application.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finaltica.application.entity.Category;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.CategoryType;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

	List<Category> findByUserIsNullOrUser(User user);

	@Query("SELECT c FROM Category c WHERE (c.type = :type AND c.user IS NULL) OR (c.type = :type AND c.user = :user)")
	List<Category> findByTypeAndUserOrGlobal(@Param("type") CategoryType type, @Param("user") User user);

	boolean existsByNameAndTypeAndUser(String name, CategoryType type, User user);

	@Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Transaction t WHERE t.category.id = :categoryId")
	boolean hasTransactions(@Param("categoryId") UUID categoryId);

	boolean existsByNameAndTypeAndUserIsNull(String name, CategoryType type);
}