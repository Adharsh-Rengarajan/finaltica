package com.finaltica.application.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.finaltica.application.config.JwtConfig;
import com.finaltica.application.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

		return Jwts.builder().subject(user.getId().toString()).claim("email", user.getEmail()).issuedAt(now)
				.expiration(expiryDate).signWith(getSigningKey()).compact();
	}
}