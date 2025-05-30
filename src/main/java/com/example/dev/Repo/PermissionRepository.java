package com.example.dev.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dev.Entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
	
	List<Permission> findByNameIn(List<String> names);
	
	Optional<Permission> findByName(String names);

}
