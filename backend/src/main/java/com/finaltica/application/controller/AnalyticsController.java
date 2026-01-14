package com.finaltica.application.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finaltica.application.entity.User;
import com.finaltica.application.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

	@Autowired
	private AnalyticsService analyticsService;

	@GetMapping("/networth")
	public ResponseEntity<?> getNetWorth(@AuthenticationPrincipal User user) {
		return analyticsService.getNetWorth(user);
	}

	@GetMapping("/monthly-summary")
	public ResponseEntity<?> getMonthlySummary(@RequestParam int year, @RequestParam int month,
			@AuthenticationPrincipal User user) {

		return analyticsService.getMonthlySummary(user, year, month);
	}

	@GetMapping("/category-spending")
	public ResponseEntity<?> getCategorySpending(@RequestParam Instant startDate, @RequestParam Instant endDate,
			@AuthenticationPrincipal User user) {

		return analyticsService.getCategorySpending(user, startDate, endDate);
	}
}