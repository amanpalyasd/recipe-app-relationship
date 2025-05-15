package com.example.dev.Entity;

import java.util.List;

public class FoodRequestDTO {

	private String name;
    private String description;
    private List<IngredientsQuantityDTO> ingredients;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<IngredientsQuantityDTO> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<IngredientsQuantityDTO> ingredients) {
		this.ingredients = ingredients;
	}



}
