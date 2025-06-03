package com.example.dev.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.dev.Entity.Permission;
import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;
import com.example.dev.Repo.PermissionRepository;
import com.example.dev.Repo.RoleRepository;
import com.example.dev.Repo.UserRepository;
import com.example.dev.dto.RoleChangeRequest;
import com.example.dev.dto.RoleRequestDTO;
import com.example.dev.dto.UserWithRoleDTO;

@Service
public class AdminService {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PermissionRepository permissionRepository;

	public AdminService(RoleRepository roleRepository, UserRepository userRepository,
			PermissionRepository permissionRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
	}

	public void deleteRole(Long roleId) {
		Role roleToDelete = roleRepository.findById(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

		Role defaultUserRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new RuntimeException("Default USER role not found"));
		List<User> usersWithRole = userRepository.findByRole(roleToDelete);
		for (User user : usersWithRole) {
			user.setRole(defaultUserRole);
		}
		userRepository.saveAll(usersWithRole);
		roleRepository.deleteById(roleId);
	}

	public Role createRole(RoleRequestDTO roleRequestDTO) {
		if (roleRepository.existsByName(roleRequestDTO.getRoleName().toUpperCase())) {
			throw new IllegalArgumentException("Role already exists");
		}
		Set<Permission> permissions = roleRequestDTO.getPermissions().stream()
				.map(name -> permissionRepository.findByName(name)
						.orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name)))
				.collect(Collectors.toSet());

		Role role = new Role();
		role.setName(roleRequestDTO.getRoleName().toUpperCase());
		role.setPermissions(permissions);
		return roleRepository.save(role);
	}

	public void changeUserRole(Long userId, RoleChangeRequest request) {
		if (request == null || request.getRoleName() == null || request.getRoleName().isBlank()) {
			throw new IllegalArgumentException("Role name must be provided");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		String roleName = request.getRoleName().toUpperCase();

		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

		user.setRole(role);
		userRepository.save(user);
	}

	public void assignPermissionsToRole(Long roleId, List<String> permissionNames) {
		Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

		List<Permission> permissions = permissionRepository.findByNameIn(permissionNames);
		if (permissions.isEmpty()) {
			throw new RuntimeException("No valid permissions found");
		}

		role.getPermissions().addAll(permissions);
		roleRepository.save(role);
	}

	public List<UserWithRoleDTO> getAllUsersWithRoles() {
		return userRepository.findAllUsersWithRoles(); // or use findAllWithRoles() if you want roles eagerly
	}

}
