package com.fitcheckme.FitCheckMe.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
		Map<String, Object> errors = new HashMap<>();
		
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
			errors.put("message", "Token Expired");
			errors.put("details", e.getMessage());
			res.setStatus(HttpStatus.UNAUTHORIZED.value());
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);

			mapper.writeValue(res.getWriter(), errors);
			return;
		}
		catch (Exception e) {
			errors.put("message", "Authentication Error");
			errors.put("details", e.getMessage());
			res.setStatus(HttpStatus.FORBIDDEN.value());
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);

			mapper.writeValue(res.getWriter(), errors);
			return;
		}
		filterChain.doFilter(req, res);
	}
}
