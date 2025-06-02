package com.example.dev.Controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dev.Entity.Role;
import com.example.dev.Repo.PermissionRepository;
import com.example.dev.Repo.RoleRepository;
import com.example.dev.Service.AdminService;
import com.example.dev.dto.PermissionAssignRequestDTO;
import com.example.dev.dto.RoleChangeRequest;
import com.example.dev.dto.RoleRequestDTO;
import com.example.dev.dto.UserWithRoleDTO;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


	@Autowired
	private AdminService adminService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/createRole")
	public ResponseEntity<?> createRole(@RequestBody RoleRequestDTO roleRequestDTO) {
	    try {
	        Role role = adminService.createRole(roleRequestDTO);
	        return ResponseEntity.ok("Role created successfully");
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
	    }
	}
		
	
	//  Change role of a user - only admin with MANAGE_ROLES permission
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/assign-role/{userId}")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @RequestBody RoleChangeRequest request) {
		System.out.println("ENTRY");
        try {
        	adminService.changeUserRole(userId, request);
        	return ResponseEntity.ok(Map.of("message", "User role updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user role: " + e.getMessage()));
        }
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAllUsers")
    public List<UserWithRoleDTO> getAllUsers() {
        return adminService.getAllUsersWithRoles();
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all-roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = roleRepository.findAll()
                                           .stream()
                                           .map(Role::getName)
                                           .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/permissions")
    public ResponseEntity<List<String>> getAllPermissions() {
        List<String> permissions = permissionRepository.findAll()
                .stream()
                .map(permission -> permission.getName()) // getName() should return permission name
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }

}
