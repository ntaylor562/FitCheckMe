package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitcheckme.FitCheckMe.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	Optional<Role> findByRoleName(String roleName);
	Optional<Role> findByRoleNameIgnoreCase(String roleName);

	List<Role> findByRoleNameIn(Iterable<String> roleNames);
}
