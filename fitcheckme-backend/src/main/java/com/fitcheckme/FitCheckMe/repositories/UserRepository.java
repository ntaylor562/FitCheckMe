package com.fitcheckme.FitCheckMe.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fitcheckme.FitCheckMe.models.Role;
import com.fitcheckme.FitCheckMe.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByEmailIgnoreCase(String email);

	Optional<User> findByUsernameIgnoreCase(String username);
	Optional<User> findByEmailIgnoreCase(String email);
	List<User> findAllByOrderByIdAsc();

	@Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = ?1")
	List<Role> findRolesByUserId(Integer id);

	@Modifying
	@Query(value = "DELETE FROM app.user_following WHERE user_id = ?1 OR following_id = ?1", nativeQuery = true)
	void deleteUserFollowings(Integer userId);

	@Modifying
	@Query(value = "DELETE FROM app.user_role WHERE user_id = ?1", nativeQuery = true)
	void deleteUserRoles(Integer userId);

	@Modifying
	@Query(value = "DELETE FROM app.garment WHERE user_id = ?1", nativeQuery = true)
	void deleteUserGarments(Integer userId);
}
