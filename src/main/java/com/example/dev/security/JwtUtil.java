package com.example.dev.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.dev.Entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	// IMPORTANT: Use at least 256-bit secret key (Base64-encoded)
	private static final String SECRET_KEY = "MzJieXRlc2xvbmdzZWNyZXRrZXlmb3Jqc3V0aWx1dGlsaXR5"; // e.g. Base64 of 32
	private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 24;																				// chars

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

/*
	// 🧾 Generate token with empty claims
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", userDetails.getUsername());
		
		String roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		claims.put("role", roles);
		return createToken(claims, userDetails.getUsername(), ACCESS_TOKEN_VALIDITY);

	}
*/
	
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", userDetails.getUsername());
		
		var roles = userDetails.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .filter(auth -> auth.startsWith("ROLE_"))
	            .map(role -> role.substring(5)) // Remove "ROLE_" prefix
	            .collect(Collectors.toList());
		
		var permissions = userDetails.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .filter(auth -> !auth.startsWith("ROLE_"))
	            .collect(Collectors.toList());
		
		
		claims.put("roles", roles);
		claims.put("permissions", permissions);
		return createToken(claims, userDetails.getUsername(), ACCESS_TOKEN_VALIDITY);
	}
	
	
	
	public String createToken(Map<String, Object> claims, String subject, long validity) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + validity))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	public Long extractUserId(String token) {
		return Long.valueOf(extractAllClaims(token).get("id").toString());
	}

	public String extractUserRole(String token) {
		return extractAllClaims(token).get("role").toString();
	}

}
