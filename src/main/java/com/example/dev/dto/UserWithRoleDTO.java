package com.example.dev.dto;

public class UserWithRoleDTO {
	
	    private Long id;
	    private String username;
	    private String roleName;
		public UserWithRoleDTO(Long id, String username, String roleName) {
			super();
			this.id = id;
			this.username = username;
			this.roleName = roleName;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getRoleName() {
			return roleName;
		}
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}
	    
	    

}
