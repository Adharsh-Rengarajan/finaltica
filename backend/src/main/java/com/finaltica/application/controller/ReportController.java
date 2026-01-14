package com.finaltica.application.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finaltica.application.dto.ApiResponse;
import com.finaltica.application.entity.User;
import com.finaltica.application.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("/monthly")
	public ResponseEntity<?> generateMonthlyReport(@RequestParam int year, @RequestParam int month,
			@AuthenticationPrincipal User user) {

		String downloadUrl = reportService.generateMonthlyReport(user, year, month);

		Map<String, String> data = new HashMap<>();
		data.put("downloadUrl", downloadUrl);

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Monthly report generated successfully", data));
	}

	@GetMapping("/custom")
	public ResponseEntity<?> generateCustomReport(@RequestParam Instant startDate, @RequestParam Instant endDate,
			@AuthenticationPrincipal User user) {

		String downloadUrl = reportService.generateCustomReport(user, startDate, endDate);

		Map<String, String> data = new HashMap<>();
		data.put("downloadUrl", downloadUrl);

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Custom report generated successfully", data));
	}
}