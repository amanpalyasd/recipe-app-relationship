package com.example.dev.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.dev.Entity.Role;
import com.example.dev.Entity.User;
import com.example.dev.Repo.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {
	
	 @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    
	    @PostConstruct
	    public void init() {
	        if (userRepository.count() == 0) {
	            User admin = new User();
	            admin.setUsername("admin");
	            admin.setPassword(passwordEncoder.encode("admin123"));
	            admin.setRole(Role.ADMIN);
	            userRepository.save(admin);
	            System.out.println("Default admin user created.");
	        }
	    }

}
