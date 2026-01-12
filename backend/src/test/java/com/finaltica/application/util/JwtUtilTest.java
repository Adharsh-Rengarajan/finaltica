package com.finaltica.application.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finaltica.application.config.JwtConfig;
import com.finaltica.application.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

	@Mock
	private JwtConfig jwtConfig;

	@InjectMocks
	private JwtUtil jwtUtil;

	private User testUser;
	private String testSecret;
	private Long testExpiration;

	@BeforeEach
	void setUp() {
		// Test configuration
		testSecret = "mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong";
		testExpiration = (long) 1800000; // 1 hour

		// Mock JwtConfig
		when(jwtConfig.getSecret()).thenReturn(testSecret);
		when(jwtConfig.getExpiration()).thenReturn(testExpiration);

		// Create test user
		testUser = new User();
		testUser.setId(UUID.randomUUID());
		testUser.setEmail("test@example.com");
		testUser.setFirstName("Test");
		testUser.setLastName("User");
	}

	@Test
	void generateToken_ShouldReturnNonNullToken() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		assertNotNull(token);
		assertTrue(token.length() > 0);
	}

	@Test
	void generateToken_ShouldContainThreeParts() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		String[] parts = token.split("\\.");
		assertEquals(3, parts.length, "JWT should have 3 parts: header.payload.signature");
	}

	@Test
	void generateToken_ShouldContainUserIdInSubject() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes())).build()
				.parseSignedClaims(token).getPayload();

		assertEquals(testUser.getId().toString(), claims.getSubject());
	}

	@Test
	void generateToken_ShouldContainEmailClaim() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes())).build()
				.parseSignedClaims(token).getPayload();

		assertEquals(testUser.getEmail(), claims.get("email"));
	}

	@Test
	void generateToken_ShouldHaveIssuedAtClaim() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes())).build()
				.parseSignedClaims(token).getPayload();

		assertNotNull(claims.getIssuedAt());
	}

	@Test
	void generateToken_ShouldHaveExpirationClaim() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes())).build()
				.parseSignedClaims(token).getPayload();

		assertNotNull(claims.getExpiration());
		assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
	}

	@Test
	void generateToken_ShouldHaveCorrectExpirationTime() {
		// Arrange
		long beforeGeneration = System.currentTimeMillis();

		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert
		long afterGeneration = System.currentTimeMillis();

		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes())).build()
				.parseSignedClaims(token).getPayload();

		long expirationTime = claims.getExpiration().getTime();
		long issuedAtTime = claims.getIssuedAt().getTime();

		// Calculate actual duration between issuedAt and expiration
		long actualDuration = expirationTime - issuedAtTime;

		// Allow small tolerance (100ms) for processing time
		long tolerance = 100;

		assertTrue(Math.abs(actualDuration - testExpiration) <= tolerance,
				String.format("Expected expiration duration to be %d ms, but was %d ms (difference: %d ms)",
						testExpiration, actualDuration, Math.abs(actualDuration - testExpiration)));

		// Also verify expiration is in the future
		assertTrue(expirationTime > afterGeneration, "Expiration should be in the future");
	}

	@Test
	void generateToken_ForDifferentUsers_ShouldGenerateDifferentTokens() {
		// Arrange
		User user1 = new User();
		user1.setId(UUID.randomUUID());
		user1.setEmail("user1@example.com");

		User user2 = new User();
		user2.setId(UUID.randomUUID());
		user2.setEmail("user2@example.com");

		// Act
		String token1 = jwtUtil.generateToken(user1);
		String token2 = jwtUtil.generateToken(user2);

		// Assert
		assertNotNull(token1);
		assertNotNull(token2);
		assertTrue(!token1.equals(token2), "Tokens for different users should be different");
	}

	@Test
	void generateToken_ShouldBeVerifiableWithSameSecret() {
		// Act
		String token = jwtUtil.generateToken(testUser);

		// Assert - Should not throw exception
		Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(testSecret.getBytes())).build()
				.parseSignedClaims(token).getPayload();

		assertNotNull(claims);
	}
}