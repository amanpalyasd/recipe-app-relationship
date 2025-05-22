package com.example.dev.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dev.Entity.Food;
import com.example.dev.Entity.FoodRequestDTO;
import com.example.dev.Entity.FoodResponseDTO;
import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;
import com.example.dev.Repo.UserRepository;
import com.example.dev.Service.FoodService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/foods")
public class FoodController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FoodService foodService;

	// Endpoint to create a new food

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<?> createFood(@RequestBody FoodRequestDTO foodRequestDTO, Authentication authentication) {
		try {
			FoodResponseDTO createdFood = foodService.createFood(foodRequestDTO, authentication);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	@GetMapping
	public ResponseEntity<List<FoodResponseDTO>> getAllFoods() {
		return ResponseEntity.ok(foodService.getAllFoodWithIngredients());
	}
	
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateFood(@PathVariable Long id, 
	                                    @RequestBody FoodRequestDTO foodRequestDTO,
	                                    Authentication authentication) {
	    try {
	        FoodResponseDTO updatedFood = foodService.updateFood(id, foodRequestDTO, authentication);
	        return ResponseEntity.ok(updatedFood);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
	    }
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteFood(@PathVariable Long id, Authentication authentication) {
	    try {
	        foodService.deleteFood(id, authentication);
	        return ResponseEntity.ok(Map.of("message", "Food deleted successfully"));
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
	    }
	}
	
	
}
