package com.example.dev.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dev.Entity.Food;
import com.example.dev.Entity.FoodRequestDTO;
import com.example.dev.Entity.FoodResponseDTO;
import com.example.dev.Entity.User;
import com.example.dev.Repo.UserRepository;
import com.example.dev.Service.FoodService;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FoodService foodService;

	// Endpoint to create a new food
	@PostMapping("/create")
	public ResponseEntity<?> createFood(@RequestBody FoodRequestDTO foodRequestDTO, @RequestParam Long chefId) {
		try {

			System.out.println("CREATE");
			FoodResponseDTO createdFood = foodService.createFood(foodRequestDTO, chefId);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(createdFood);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@RequestBody User user) {
	    return ResponseEntity.ok(userRepository.save(user));
	}
	
	@GetMapping
    public ResponseEntity<List<FoodResponseDTO>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoodWithIngredients());
    }
}
