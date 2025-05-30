package com.example.dev.security;



import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.dev.Entity.Permission;
import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;
import com.example.dev.Repo.PermissionRepository;
import com.example.dev.Repo.RoleRepository;
import com.example.dev.Repo.UserRepository;


@Component
@Configuration
public class DataInitializer{
	
	@Bean
    public CommandLineRunner seedData(RoleRepository roleRepo, PermissionRepository permRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
        	 List<String> allPerms = List.of("ADD_FOOD", "UPDATE_FOOD", "DELETE_FOOD", "VIEW_FOOD");
        	 
        	 Set<Permission> permissionSet = allPerms.stream()
        	            .map(p -> permRepo.findByName(p).orElseGet(() -> permRepo.save(new Permission(p))))
        	            .collect(Collectors.toSet());
        	 
        	 Role adminRole = roleRepo.findByName("ADMIN")
        	            .orElseGet(() -> {
        	                Role r = new Role();
        	                r.setName("ADMIN");
        	                r.setPermissions(permissionSet);
        	                return roleRepo.save(r);
        	            });
        	 
        	 if (userRepo.findByUsername("admin").isEmpty()) {
                 User admin = new User();
                 admin.setUsername("admin");
                 admin.setPassword(passwordEncoder.encode("admin123"));
                 admin.setRole(adminRole);
                 userRepo.save(admin);
             }
        	 
        	 Permission viewFoodPermission = permRepo.findByName("VIEW_FOOD")
                     .orElseThrow(() -> new RuntimeException("VIEW_FOOD permission not found"));

                 Set<Permission> userPermissions = new HashSet<>();
                 userPermissions.add(viewFoodPermission);

                 Role userRole = roleRepo.findByName("USER")
                     .orElseGet(() -> {
                         Role r = new Role();
                         r.setName("USER");
                         r.setPermissions(userPermissions); // only view permission
                         return roleRepo.save(r);
                     });
        	 
         };
		
         

}
	}
