package com.fitcheckme.FitCheckMe.auth;

import org.springframework.security.core.userdetails.User;

public class TestCustomUserDetails extends User {

	private final Integer userId;

	public TestCustomUserDetails(Integer userId, String username, String password, boolean enabled) {
		super(username, password, enabled, true, true, true, null);
		this.userId = userId;
	}

	public Integer userId() {
		return this.userId;
	}
}
