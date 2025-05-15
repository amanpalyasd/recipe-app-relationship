package com.example.dev.Entity;

public class IngredientResponseDTO {
	
	private String name;
    private String quantity;

    public IngredientResponseDTO(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public IngredientResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
    

}
