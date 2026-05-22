package com.finaltica.application.controller;

import java.time.Duration;
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

	private static final Duration MAX_REPORT_RANGE = Duration.ofDays(366);
	private static final int MIN_YEAR = 2000;
	private static final int MAX_YEAR = 2100;

	@Autowired
	private ReportService reportService;

	@GetMapping("/monthly")
	public ResponseEntity<?> generateMonthlyReport(@RequestParam int year, @RequestParam int month,
			@AuthenticationPrincipal User user) {

		if (month < 1 || month > 12) {
			Map<String, String> errors = new HashMap<>();
			errors.put("month", "Month must be between 1 and 12");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid month", errors));
		}
		if (year < MIN_YEAR || year > MAX_YEAR) {
			Map<String, String> errors = new HashMap<>();
			errors.put("year", "Year must be between " + MIN_YEAR + " and " + MAX_YEAR);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid year", errors));
		}

		String downloadUrl = reportService.generateMonthlyReport(user, year, month);

		Map<String, String> data = new HashMap<>();
		data.put("downloadUrl", downloadUrl);

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Monthly report generated successfully", data));
	}

	@GetMapping("/custom")
	public ResponseEntity<?> generateCustomReport(@RequestParam Instant startDate, @RequestParam Instant endDate,
			@AuthenticationPrincipal User user) {

		if (startDate == null || endDate == null) {
			Map<String, String> errors = new HashMap<>();
			errors.put("dateRange", "startDate and endDate are required");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid date range", errors));
		}
		if (!startDate.isBefore(endDate)) {
			Map<String, String> errors = new HashMap<>();
			errors.put("dateRange", "startDate must be before endDate");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid date range", errors));
		}
		if (Duration.between(startDate, endDate).compareTo(MAX_REPORT_RANGE) > 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("dateRange",
					"Date range cannot exceed " + MAX_REPORT_RANGE.toDays() + " days. Use multiple reports instead.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Date range too large", errors));
		}

		String downloadUrl = reportService.generateCustomReport(user, startDate, endDate);

		Map<String, String> data = new HashMap<>();
		data.put("downloadUrl", downloadUrl);

		return ResponseEntity
				.ok(ApiResponse.success(HttpStatus.OK.value(), "Custom report generated successfully", data));
	}
}