package com.example.dev.Entity;

import java.util.List;

public class FoodResponseDTO {
	
	private Long id;
    private String name;
    private String description;
    private List<IngredientResponseDTO> ingredients;
    
    
     
    
	public FoodResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FoodResponseDTO(String name, String description) {
		super();
		this.name = name;
		this.description = description;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<IngredientResponseDTO> getIngredients() {
		return ingredients;
	}
	public void setIngredients(List<IngredientResponseDTO> ingredients) {
		this.ingredients = ingredients;
	}
    
    
    

}
