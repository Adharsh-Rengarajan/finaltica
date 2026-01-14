package com.finaltica.application.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finaltica.application.dto.CreateCategoryRequestDTO;
import com.finaltica.application.dto.UpdateCategoryRequestDTO;
import com.finaltica.application.entity.User;
import com.finaltica.application.enums.CategoryType;
import com.finaltica.application.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public ResponseEntity<?> getCategories(
			@RequestParam(required = false) CategoryType type,
			@AuthenticationPrincipal User user) {
		
		if (type != null) {
			return categoryService.getCategoriesByType(type, user);
		}
		return categoryService.getAllCategories(user);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getCategoryById(
			@PathVariable UUID id,
			@AuthenticationPrincipal User user) {
		
		return categoryService.getCategoryById(id, user);
	}

	@PostMapping
	public ResponseEntity<?> createCategory(
			@Valid @RequestBody CreateCategoryRequestDTO createCategoryRequestDTO,
			@AuthenticationPrincipal User user) {
		
		return categoryService.createCategory(createCategoryRequestDTO, user);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateCategory(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateCategoryRequestDTO updateCategoryRequestDTO,
			@AuthenticationPrincipal User user) {
		
		return categoryService.updateCategory(id, updateCategoryRequestDTO, user);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(
			@PathVariable UUID id,
			@AuthenticationPrincipal User user) {
		
		return categoryService.deleteCategory(id, user);
	}
}