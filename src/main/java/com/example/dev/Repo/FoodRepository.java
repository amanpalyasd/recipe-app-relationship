package com.example.dev.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dev.Entity.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

	Optional<Food> findByName(String name);
	
	
	
//	Optional<Food> findByIdWithIngredients(@Param("id") Long id);

}
