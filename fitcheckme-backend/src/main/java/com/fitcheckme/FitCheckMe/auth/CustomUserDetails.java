package com.fitcheckme.FitCheckMe.auth;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fitcheckme.FitCheckMe.models.Role;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

	private final Integer userId;

	private final String username;

	private String password;

	private final Set<? extends GrantedAuthority> authorities;

	private final boolean accountNonExpired;

	private final boolean accountNonLocked;

	private final boolean credentialsNonExpired;

	private final boolean enabled;

	public CustomUserDetails(Integer userId, String username, String password,
			Set<? extends GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
			boolean credentialsNonExpired, boolean enabled) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.enabled = enabled;
	}

	public CustomUserDetails(Integer userId, String username, String password, Set<Role> authorities) {
		if(authorities == null) {
			this.authorities = null;
		} else {
			this.authorities = authorities.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).collect(Collectors.toSet());
		}
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
	}

	public Integer getUserId() {
		return this.userId;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void eraseCredentials() {
		this.password = null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CustomUserDetails) {
			return this.userId.equals(((CustomUserDetails) obj).getUserId());
		}
		return false;
	}

}
