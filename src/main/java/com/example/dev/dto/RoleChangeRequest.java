package com.example.dev.dto;

public class RoleChangeRequest {
	
	private String roleName;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public RoleChangeRequest(String roleName) {
		super();
		this.roleName = roleName;
	}

	public RoleChangeRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
