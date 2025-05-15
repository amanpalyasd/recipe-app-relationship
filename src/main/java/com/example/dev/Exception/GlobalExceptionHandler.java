package com.example.dev.Exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
		return new ResponseEntity<>("You do not have permission to access this resource.", HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(InvalidUsernameAndPassword.class)
	public ResponseEntity<?> handleUserNotFoundException(InvalidUsernameAndPassword ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("timestamp", LocalDateTime.now(), "error",
				ex.getMessage(), "status", HttpStatus.UNAUTHORIZED.value()));
	}
}