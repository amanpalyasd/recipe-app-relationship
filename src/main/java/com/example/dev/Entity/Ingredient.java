package com.example.dev.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "ingredient")
	@JsonIgnore
	private List<FoodIngredients> foodIngredients = new ArrayList<>();

	public Ingredient(Long id, String name, List<FoodIngredients> foodIngredients) {
		super();
		this.id = id;
		this.name = name;
		this.foodIngredients = foodIngredients;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FoodIngredients> getFoodIngredients() {
		return foodIngredients;
	}

	public void setFoodIngredients(List<FoodIngredients> foodIngredients) {
		this.foodIngredients = foodIngredients;
	}

	public Ingredient() {
		super();
		// TODO Auto-generated constructor stub
	}

}
