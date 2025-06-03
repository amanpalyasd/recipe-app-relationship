package com.example.dev.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.dev.Entity.Permission;
import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;
import com.example.dev.Repo.RoleRepository;
import com.example.dev.Repo.UserRepository;
import com.example.dev.dto.AuthRequest;
import com.example.dev.dto.AuthResponse;
import com.example.dev.dto.RegisterRequestDTO;
import com.example.dev.security.JwtUtil;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
		try {

			if (userRepository.existsByUsername(request.getUsername())) {
				return ResponseEntity.badRequest()
						.body(Map.of("error", "Username '" + request.getUsername() + "' is already taken."));
			}
			String roleName = (request.getRole() == null || request.getRole().isEmpty()) ? "USER"
					: request.getRole().toUpperCase();

			Role role = roleRepository.findByName(roleName)
					.orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found."));

			User user = new User();
			user.setUsername(request.getUsername());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			user.setRole(role);
			userRepository.save(user);

			return ResponseEntity.ok("User registered successfully!");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "Invalid role provided: " + request.getRole()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Registration failed: " + e.getMessage()));
		}

	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		try {

			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			String token = jwtUtil.generateToken(userDetails);
//			List<String> permissions = user.getRole().stream()
//			        .flatMap(role -> role.getPermissions().stream())
//			        .map(Permission::getName)
//			        .distinct()
//			        .collect(Collectors.toList());
			return ResponseEntity.ok(new AuthResponse(token));
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));

		} catch (BadCredentialsException ex) {
			// This exception is thrown if username or password is incorrect
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
		}

		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Login failed: " + e.getMessage()));
		}
	}

}
