package com.example.dev.Controller;

import java.util.Map;
import java.util.Optional;

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

import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;

import com.example.dev.Repo.UserRepository;
import com.example.dev.dto.AuthRequest;
import com.example.dev.dto.AuthResponse;
import com.example.dev.dto.RegisterRequest;
import com.example.dev.security.JwtUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		try {
			Role role;
			if (request.getRole() == null || request.getRole().isEmpty()) {
				role = Role.USER; // Assign default role
			} else {
				role = Role.valueOf(request.getRole().toUpperCase());
			}
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
	
	@PreAuthorize("hasRole('ADMIN')")
	 @PostMapping("/promote/{userId}")
	    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
	      Optional<User> optionalUser = userRepository.findById(userId);
	      if(optionalUser.isPresent()) {
	    	  User user = optionalUser.get();
	    	  user.setRole(Role.ADMIN);
	    	  userRepository.save(user);
	    	  return ResponseEntity.ok("User promoted to ADMIN role successfully.");
	      }else {
	    	  return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
	      }
	    }

}
