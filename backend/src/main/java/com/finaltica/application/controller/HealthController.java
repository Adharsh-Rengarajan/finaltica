package com.finaltica.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class HealthController {
	@GetMapping("/health")
	public ResponseEntity<String> getHealth() {
		return ResponseEntity.ok("API is running succesfully");
	}
}
