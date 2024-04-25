package com.fitcheckme.FitCheckMe.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.fitcheckme.FitCheckMe.DTOs.User.JwtUserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
	
	private final SecretKey secretKey;

	@Value("${fitcheckme.jwt-access-token-validity-s}")
	private Integer jwtAccessTokenValidity;

	private final String TOKEN_HEADER = "Authorization";
	private final String TOKEN_PREFIX = "Bearer ";

	private final JwtParser jwtParser;

	public JwtUtil(@Value("${fitcheckme.jwt-secret-key}") String secretString) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
		this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
	}

	public String createToken(JwtUserDTO user) {		
		return Jwts.builder()
			.id(Integer.toString(user.userId()))
			.subject(user.username())
			.claim("em", user.email())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + jwtAccessTokenValidity * 1000))
			.signWith(secretKey)
			.compact();
	}

	private Claims parseJwtClaims(String token) {
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(TOKEN_HEADER);
		if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
			return bearerToken.substring(TOKEN_PREFIX.length());
		}
		return null;
	}

	public Claims resolveClaims(HttpServletRequest req) {
		try {
			String token = resolveToken(req);
			if(token != null) {
				return parseJwtClaims(token);
			}
			return null;
		}
		catch(ExpiredJwtException e) {
			req.setAttribute("expired", e.getMessage());
			throw e;
		}
		catch(Exception e) {
			req.setAttribute("invalid", e.getMessage());
			throw e;
		}
	}

	public boolean validateClaims(Claims claims) throws AuthenticationException {
		try {
			return claims.getExpiration().after(new Date());
		}
		catch (Exception e) {
			throw e;
		}
	}

	public String getUsername(Claims claims) {
		return claims.getSubject();
	}
}