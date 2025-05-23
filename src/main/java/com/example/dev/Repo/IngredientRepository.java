package com.example.dev.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dev.Entity.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long>{

	Optional<Ingredient> findByName(String name);

}
