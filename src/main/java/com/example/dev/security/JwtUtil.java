package com.example.dev.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	// IMPORTANT: Use at least 256-bit secret key (Base64-encoded)
	private static final String SECRET_KEY = "MzJieXRlc2xvbmdzZWNyZXRrZXlmb3Jqc3V0aWx1dGlsaXR5"; // e.g. Base64 of 32
																									// chars

	// 🔐 Decode Base64 and return Key object for signing
	private Key getSignInKey() {
		byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// 🔑 Extract username (subject) from token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// 📆 Extract expiration
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// 🔄 Generic claim extractor
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// 📦 Parse and extract claims
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	// 📌 Check if token expired
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// ✅ Validate token against user details
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// 🧾 Generate token with empty claims
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	// 🏷️ Generate token with custom claims
	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

}
