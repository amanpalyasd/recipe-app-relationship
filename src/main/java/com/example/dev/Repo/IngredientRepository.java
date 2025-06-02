package com.example.dev.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dev.Entity.Ingredient;

import jakarta.transaction.Transactional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long>{

	Optional<Ingredient> findByName(String name);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM Ingredient i WHERE SIZE(i.foodIngredients) = 0")
	void deleteUnusedIngredients();
}
