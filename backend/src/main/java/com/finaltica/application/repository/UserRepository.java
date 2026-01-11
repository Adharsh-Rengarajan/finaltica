package com.finaltica.application.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finaltica.application.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	boolean existsByEmail(String email);
}
