package com.fitcheckme.FitCheckMe.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fitcheckme.FitCheckMe.models.User;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UsernameNotFoundException notFoundException = new UsernameNotFoundException(String.format("User with username '%s' not found", username));

		//Getting user by username or email
		User user = username.contains("@")
			? userRepository.findByEmailIgnoreCase(username).orElseThrow(() -> notFoundException)
			: userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> notFoundException);


		//TODO implement roles to return roles in userDetails
		List<String> roles = new ArrayList<>();
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(user.getUsername())
			.password(user.getPassword())
			.roles(roles.toArray(new String[0]))
			.build();
		return userDetails;
	}
}
