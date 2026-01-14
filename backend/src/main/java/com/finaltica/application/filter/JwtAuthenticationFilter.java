package com.finaltica.application.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.finaltica.application.entity.User;
import com.finaltica.application.repository.UserRepository;
import com.finaltica.application.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		try {
			String userId = jwtUtil.extractUserId(token);

			User user = userRepository.findById(java.util.UUID.fromString(userId))
					.orElseThrow(() -> new RuntimeException("User not found"));

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
					java.util.Collections.emptyList());

			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception e) {
			System.err.println("JWT validation failed: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}