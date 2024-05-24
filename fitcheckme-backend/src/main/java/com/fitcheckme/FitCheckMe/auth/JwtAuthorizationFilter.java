package com.fitcheckme.FitCheckMe.auth;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.ExceptionResponseDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Lazy
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{
	
	private final JwtUtil jwtUtil;

	private final CustomUserDetailsService userDetailsService;
	
	private final ObjectMapper mapper;

	public JwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, ObjectMapper mapper) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = customUserDetailsService;
		this.mapper = mapper;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			String accessToken = jwtUtil.resolveToken(req);
			if(accessToken == null) {
				filterChain.doFilter(req, res);
				return;
			}
			Claims claims = null;
			try {
				claims = jwtUtil.resolveClaims(req);
			}
			catch(ExpiredJwtException e) {
				if(req.getServletPath().equals("/api/auth/refresh") 
					|| req.getServletPath().equals("/api/auth/logout")
					|| req.getServletPath().equals("/api/auth/login")
					|| req.getServletPath().equals("/api/user/create")) {
					filterChain.doFilter(req, res);
					return;
				}
				else throw e;
			}

			if(claims != null && jwtUtil.validateClaims(claims)) {
				UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

				//Empty credentials because authentication was successful
				Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

				//Marking authentication as successful
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		catch (ExpiredJwtException e) {
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);

			mapper.writeValue(res.getWriter(), new ExceptionResponseDTO("Token Expired", e.getMessage()));
			return;
		}
		catch (Exception e) {
			res.setStatus(HttpStatus.FORBIDDEN.value());
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);

			mapper.writeValue(res.getWriter(), new ExceptionResponseDTO("Authentication Error", e.getMessage()));
			return;
		}
		filterChain.doFilter(req, res);
	}
}
