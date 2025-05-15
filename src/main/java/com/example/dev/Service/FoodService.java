package com.example.dev.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

	public FoodResponseDTO createFood(FoodRequestDTO foodRequestDTO, Long chefId) {
		// Step 1: Retrieve the chef (User) from the database using the chefId
		User chef = userRepository.findById(chefId).orElseThrow(() -> new RuntimeException("Chef not found"));

		// Step 2: Check if a food with the same name already exists in the database
		if (foodRepository.findByName(foodRequestDTO.getName()).isPresent()) {
			throw new RuntimeException("Food with this name already exists.");
		}

		// Step 4: Create and populate a new Food entity with the data from the request
		// DTO
		Food food = new Food();
		food.setName(foodRequestDTO.getName()); // Set the food name
		food.setDescription(foodRequestDTO.getDescription()); // Set the food description
		food.setCreatedBy(chef); // Associate the food with the chef who created it

		// Step 5: Save the food entity to the database
		food = foodRepository.save(food);

		// System.out.println("------------------++++++++++++"+ingredients);

		// System.out.println("Ingredients size: " + foodRequestDTO.getIngredients());

		// Step 6: Loop through the list of ingredients and add them to the food
		for (IngredientsQuantityDTO ingredientDTO : foodRequestDTO.getIngredients()) {
			// Step 6.1: Check if the ingredient already exists in the database

			Ingredient ingredient = ingredientRepository.findByName(ingredientDTO.getName()).orElseGet(() -> {
				// If the ingredient doesn't exist, create a new one and save it
				Ingredient newIngredient = new Ingredient();
				newIngredient.setName(ingredientDTO.getName());
				return ingredientRepository.save(newIngredient);
			});

			// Step 6.2: Check if the food-ingredient combination already exists
			Optional<FoodIngredients> existingFoodIngredient = foodIngredientRepository.findByFoodAndIngredient(food,
					ingredient);
			if (existingFoodIngredient.isPresent()) {
				continue; // Skip if the combination already exists (to avoid duplicates)
			}

			// Step 6.3: Create and save the food-ingredient relationship
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

		FoodResponseDTO responseDTO = new FoodResponseDTO();
		responseDTO.setId(food.getId());
		responseDTO.setName(food.getName());
		responseDTO.setDescription(food.getDescription());
		responseDTO.setIngredients(ingredients);
		System.out.println("Ingredients size: " + food.getFoodIngredients().size());
		return responseDTO;

	}

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

}
