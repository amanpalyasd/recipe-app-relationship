package com.example.dev.dto;

import java.util.Set;

public class RoleRequestDTO {
	
	private String roleName;
	
	private Set<String> permissions;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
	
	
	

}
