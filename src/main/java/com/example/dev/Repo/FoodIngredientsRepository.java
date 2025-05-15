package com.example.dev.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dev.Entity.Food;
import com.example.dev.Entity.FoodIngredients;
import com.example.dev.Entity.Ingredient;

@Repository
public interface FoodIngredientsRepository extends JpaRepository<FoodIngredients, Long>{
	
	Optional<FoodIngredients> findByFoodAndIngredient(Food food, Ingredient ingredients);

}
