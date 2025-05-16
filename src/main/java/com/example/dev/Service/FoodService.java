package com.example.dev.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.dev.Entity.Food;
import com.example.dev.Entity.FoodIngredients;
import com.example.dev.Entity.FoodRequestDTO;
import com.example.dev.Entity.FoodResponseDTO;
import com.example.dev.Entity.Ingredient;
import com.example.dev.Entity.IngredientResponseDTO;
import com.example.dev.Entity.IngredientsQuantityDTO;
import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;
import com.example.dev.Repo.FoodIngredientsRepository;
import com.example.dev.Repo.FoodRepository;
import com.example.dev.Repo.IngredientRepository;
import com.example.dev.Repo.UserRepository;

@Service
public class FoodService {

	@Autowired
	private FoodRepository foodRepository;

	@Autowired
	private IngredientRepository ingredientRepository;

	@Autowired
	private FoodIngredientsRepository foodIngredientRepository;

	@Autowired
	private UserRepository userRepository;

	/* ADDING FOOD METHOD */
	public FoodResponseDTO createFood(FoodRequestDTO foodRequestDTO, Authentication authentication) {

		String username = authentication.getName();
		User chef = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		if (foodRepository.findByName(foodRequestDTO.getName()).isPresent()) {
			throw new RuntimeException("Food with this name already exists.");
		}
		Food food = new Food();
		food.setName(foodRequestDTO.getName()); // Set the food name
		food.setDescription(foodRequestDTO.getDescription()); // Set the food description
		food.setCreatedBy(chef); // Associate the food with the chef who created it

		food = foodRepository.save(food);
		for (IngredientsQuantityDTO ingredientDTO : foodRequestDTO.getIngredients()) {
			Ingredient ingredient = ingredientRepository.findByName(ingredientDTO.getName()).orElseGet(() -> {
				Ingredient newIngredient = new Ingredient();
				newIngredient.setName(ingredientDTO.getName());
				return ingredientRepository.save(newIngredient);
			});

			// Avoid duplicate food-ingredient combinations
			Optional<FoodIngredients> existingFoodIngredient = foodIngredientRepository.findByFoodAndIngredient(food,
					ingredient);
			if (existingFoodIngredient.isPresent()) {
				continue; // Skip if the combination already exists (to avoid duplicates)
			}

			FoodIngredients foodIngredient = new FoodIngredients();
			foodIngredient.setFood(food);
			foodIngredient.setIngredient(ingredient);

			foodIngredient.setQuantity(ingredientDTO.getQuantity()); // Set the quantity
			food.getFoodIngredients().add(foodIngredient);
			foodIngredientRepository.save(foodIngredient); // Save the food-ingredient relation
		}
		List<IngredientResponseDTO> ingredients = food.getFoodIngredients().stream()
				.map(fi -> new IngredientResponseDTO(fi.getIngredient().getName(), fi.getQuantity()))
				.collect(Collectors.toList());

		// Step 7: Prepare response DTO
		FoodResponseDTO responseDTO = new FoodResponseDTO();
		responseDTO.setId(food.getId());
		responseDTO.setName(food.getName());
		responseDTO.setDescription(food.getDescription());
		responseDTO.setIngredients(ingredients);
		System.out.println("Ingredients size: " + food.getFoodIngredients().size());
		return responseDTO;

	}

	/* GET ALL FOOD FROM DATABASE */
	public List<FoodResponseDTO> getAllFoodWithIngredients() {
		System.out.println("GET ALL FOOD AND LIST OF INGREDIENTS");
		List<Food> foods = foodRepository.findAll(); // Consider fetch join if foodIngredients are LAZY
		List<FoodResponseDTO> responseList = new ArrayList<>();

		for (Food food : foods) {
			// Create DTO for each food item
			FoodResponseDTO dto = new FoodResponseDTO();
			dto.setId(food.getId());
			dto.setName(food.getName());
			dto.setDescription(food.getDescription());

			// Build list of ingredient DTOs
			List<IngredientResponseDTO> ingredientDTOs = new ArrayList<>();
			for (FoodIngredients fi : food.getFoodIngredients()) {
				IngredientResponseDTO ingredientDTO = new IngredientResponseDTO();
				ingredientDTO.setName(fi.getIngredient().getName());
				ingredientDTO.setQuantity(fi.getQuantity());
				ingredientDTOs.add(ingredientDTO);
			}

			// Set ingredients into food response DTO
			dto.setIngredients(ingredientDTOs);
			responseList.add(dto);
		}

		return responseList;
	}

	/* UPDATE FOOD BASED ON FOOD_ID */
	public FoodResponseDTO updateFood(Long foodId, FoodRequestDTO foodRequestDTO, Authentication authentication) {
		// Step 1: Get logged-in user's username
		String username = authentication.getName();

		// Step 2: Fetch the user from DB
		User admin = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		// Step 3: Ensure role is ADMIN
		if (!admin.getRole().name().equals("ADMIN")) {
			throw new AccessDeniedException("Only admins can update food.");
		}

		// Step 4: Get the food by ID
		Food food = foodRepository.findById(foodId)
				.orElseThrow(() -> new RuntimeException("Food not found with ID: " + foodId));

		// Step 5: Update food fields
		food.setName(foodRequestDTO.getName());
		food.setDescription(foodRequestDTO.getDescription());

		// Step 6: Remove old ingredient associations
		List<FoodIngredients> oldIngredients = foodIngredientRepository.findByFood(food);
		foodIngredientRepository.deleteAll(oldIngredients);
		food.getFoodIngredients().clear();

		// Step 7: Add new ingredients
		for (IngredientsQuantityDTO ingredientDTO : foodRequestDTO.getIngredients()) {
			Ingredient ingredient = ingredientRepository.findByName(ingredientDTO.getName()).orElseGet(() -> {
				Ingredient newIng = new Ingredient();
				newIng.setName(ingredientDTO.getName());
				return ingredientRepository.save(newIng);
			});

			FoodIngredients fi = new FoodIngredients();
			fi.setFood(food);
			fi.setIngredient(ingredient);
			fi.setQuantity(ingredientDTO.getQuantity());
			foodIngredientRepository.save(fi);
			food.getFoodIngredients().add(fi);
		}

		// Step 8: Save updated food
		Food updated = foodRepository.save(food);

		// Step 9: Convert to Response DTO
		List<IngredientResponseDTO> ingredients = updated.getFoodIngredients().stream()
				.map(fi -> new IngredientResponseDTO(fi.getIngredient().getName(), fi.getQuantity()))
				.collect(Collectors.toList());

		FoodResponseDTO response = new FoodResponseDTO();
		response.setId(updated.getId());
		response.setName(updated.getName());
		response.setDescription(updated.getDescription());
		response.setIngredients(ingredients);

		return response;
	}

	/* DELETE FOOD BASED ON FOODi_ID */
	public void deleteFood(Long foodId, Authentication authentication) {
		// Step 1: Get logged-in user's username
		String username = authentication.getName();

		// Step 2: Fetch the user from DB
		User admin = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		// Step 3: Check if the user has admin role
		if (!admin.getRole().name().equals("ADMIN")) {
			throw new AccessDeniedException("Only admins can delete food.");
		}

		// Step 4: Find the food by ID
		Food food = foodRepository.findById(foodId)
				.orElseThrow(() -> new RuntimeException("Food not found with ID: " + foodId));

		// Step 5: Delete related FoodIngredients entries (if needed)
		List<FoodIngredients> foodIngredients = foodIngredientRepository.findByFood(food);
		foodIngredientRepository.deleteAll(foodIngredients);

		// Step 6: Delete the food
		foodRepository.delete(food);
	}

}
