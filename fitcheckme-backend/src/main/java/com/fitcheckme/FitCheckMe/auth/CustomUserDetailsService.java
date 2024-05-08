package com.fitcheckme.FitCheckMe.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UsernameNotFoundException notFoundException = new UsernameNotFoundException(String.format("User with username '%s' not found", username));

		//Getting user by username or email
		User user = username.contains("@")
			? userRepository.findByEmailIgnoreCase(username).orElseThrow(() -> notFoundException)
			: userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> notFoundException);

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(user.getUsername())
			.password(user.getPassword())
			.roles(user.getRoles().stream().map(role -> role.getName()).toList().toArray(new String[user.getRoles().size()]))
			.build();
		return userDetails;
	}
}
