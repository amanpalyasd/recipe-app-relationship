package com.example.dev.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dev.Entity.User;
import com.example.dev.dto.UserWithRoleDTO;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findById(Long id);

	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);
	
	@Query("SELECT new com.example.dev.dto.UserWithRoleDTO(u.id, u.username, r.name) FROM User u JOIN u.role r")
    List<UserWithRoleDTO> findAllUsersWithRoles();
}
