package com.finaltica.application.filter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finaltica.application.dto.ApiResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

	private static final int MAX_REQUESTS = 10;
	private static final long WINDOW_SECONDS = 60;

	private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String path = request.getRequestURI();
		if (!path.startsWith("/api/auth/")) {
			chain.doFilter(request, response);
			return;
		}

		String key = clientIp(request) + ":" + path;
		Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(Instant.now()));

		synchronized (bucket) {
			Instant now = Instant.now();
			if (Duration.between(bucket.windowStart, now).getSeconds() > WINDOW_SECONDS) {
				bucket.windowStart = now;
				bucket.count.set(0);
			}
			int current = bucket.count.incrementAndGet();
			if (current > MAX_REQUESTS) {
				writeTooManyRequests(response);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private static String clientIp(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank()) {
			int comma = xff.indexOf(',');
			return (comma > 0 ? xff.substring(0, comma) : xff).trim();
		}
		return req.getRemoteAddr();
	}

	private void writeTooManyRequests(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType("application/json");
		Map<String, String> errors = new HashMap<>();
		errors.put("rateLimit", "Too many requests. Please wait a minute before trying again.");
		ApiResponse<Object> body = ApiResponse.error(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded",
				errors);
		response.getWriter().write(objectMapper.writeValueAsString(body));
	}

	private static final class Bucket {
		Instant windowStart;
		final AtomicInteger count = new AtomicInteger(0);

		Bucket(Instant start) {
			this.windowStart = start;
		}
	}
}