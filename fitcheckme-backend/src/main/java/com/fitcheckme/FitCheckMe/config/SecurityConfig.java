package com.fitcheckme.FitCheckMe.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fitcheckme.FitCheckMe.auth.CustomUserDetailsService;
import com.fitcheckme.FitCheckMe.auth.JwtAuthorizationFilter;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${fitcheckme.bcrypt-password-encoder-strength}")
	private Integer bCryptPasswordEncoderStrength;

	private final CustomUserDetailsService userDetailsService;
	private final JwtAuthorizationFilter jwtAuthorizationFilter;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthorizationFilter jwtAuthorizationFilter) {
		this.userDetailsService = customUserDetailsService;
		this.jwtAuthorizationFilter = jwtAuthorizationFilter;
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		return authenticationManagerBuilder.build();
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.authorizeHttpRequests(auth -> {
				auth.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll();
				auth.requestMatchers("/").permitAll();
				auth.requestMatchers("/error").permitAll();
				auth.requestMatchers("/api/user/create").permitAll();
				auth.requestMatchers("/api/auth/login").permitAll();
				auth.requestMatchers("/api/auth/refresh").permitAll();
				auth.anyRequest().authenticated();
			})
			.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
			// .httpBasic(Customizer.withDefaults())
			// .formLogin(Customizer.withDefaults())
			// .oauth2Login(Customizer.withDefaults())
			.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(bCryptPasswordEncoderStrength);
	}
}
