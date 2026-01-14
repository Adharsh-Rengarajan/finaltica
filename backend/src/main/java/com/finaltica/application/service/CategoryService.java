package com.finaltica.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.dto.CategoryResponseDTO;
import com.finaltica.application.dto.CreateCategoryRequestDTO;
import com.finaltica.application.dto.UpdateCategoryRequestDTO;
import com.finaltica.application.entity.Category;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.CategoryType;
import com.finaltica.application.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	// GET /api/categories
	public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories(User user) {
		List<Category> categories = categoryRepository.findByUserIsNullOrUser(user);
		List<CategoryResponseDTO> categoryDTOs = categories.stream().map(this::convertToDTO)
				.collect(Collectors.toList());

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Categories retrieved successfully", categoryDTOs));
	}

	// GET /api/categories?type=INCOME
	public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getCategoriesByType(CategoryType type, User user) {
		List<Category> categories = categoryRepository.findByTypeAndUserOrGlobal(type, user);
		List<CategoryResponseDTO> categoryDTOs = categories.stream().map(this::convertToDTO)
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				ApiResponse.success(HttpStatus.OK.value(), type + " categories retrieved successfully", categoryDTOs));
	}

	// GET /api/categories/{id}
	public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(UUID id, User user) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		// Security check: user can only access global OR their own categories
		if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You don't have access to this category");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		CategoryResponseDTO categoryDTO = convertToDTO(category);
		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Category retrieved successfully", categoryDTO));
	}

	// POST /api/categories
	@Transactional
	public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(CreateCategoryRequestDTO request,
			User user) {

		if (categoryRepository.existsByNameAndTypeAndUser(request.getName(), request.getType(), user)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("name",
					"You already have a " + request.getType() + " category named '" + request.getName() + "'");
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Category already exists", errors));
		}

		Category category = Category.builder().name(request.getName()).type(request.getType()).user(user).build();

		Category saved = categoryRepository.save(category);
		CategoryResponseDTO categoryDTO = convertToDTO(saved);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED.value(), "Category created successfully", categoryDTO));
	}

	// PUT /api/categories/{id}
	@Transactional
	public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(UUID id, UpdateCategoryRequestDTO request,
			User user) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		// Prevent updating global categories
		if (category.getUser() == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("category", "Global categories cannot be modified");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Cannot modify global category", errors));
		}

		// Prevent updating other users' categories
		if (!category.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You can only modify your own custom categories");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		// Check duplicate only if name changed
		if (!category.getName().equals(request.getName())) {
			if (categoryRepository.existsByNameAndTypeAndUser(request.getName(), category.getType(), user)) {
				Map<String, String> errors = new HashMap<>();
				errors.put("name",
						"You already have a " + category.getType() + " category named '" + request.getName() + "'");
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Category already exists", errors));
			}
		}

		category.setName(request.getName());
		Category updated = categoryRepository.save(category);
		CategoryResponseDTO categoryDTO = convertToDTO(updated);

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Category updated successfully", categoryDTO));
	}

	// DELETE /api/categories/{id}
	@Transactional
	public ResponseEntity<ApiResponse<Void>> deleteCategory(UUID id, User user) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		// Prevent deleting global categories
		if (category.getUser() == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("category", "Global categories cannot be deleted");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Cannot delete global category", errors));
		}

		// Prevent deleting other users' categories
		if (!category.getUser().getId().equals(user.getId())) {
			Map<String, String> errors = new HashMap<>();
			errors.put("authorization", "You can only delete your own custom categories");
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied", errors));
		}

		// Check if category is being used in transactions
		if (categoryRepository.hasTransactions(id)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("category", "Cannot delete category with existing transactions");
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "Category in use", errors));
		}

		categoryRepository.delete(category);

		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Category deleted successfully", null));
	}

	// Helper: Convert Entity to DTO
	private CategoryResponseDTO convertToDTO(Category category) {
		return CategoryResponseDTO.builder().id(category.getId()).name(category.getName()).type(category.getType())
				.isGlobal(category.getUser() == null).createdAt(category.getCreatedAt())
				.updatedAt(category.getUpdatedAt()).build();
	}
}