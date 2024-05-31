package com.fitcheckme.FitCheckMe.integration_tests;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveCookieInterceptor implements ClientHttpRequestInterceptor {

	private final String cookieNameToRemove;

	public RemoveCookieInterceptor(String cookieNameToRemove) {
		this.cookieNameToRemove = cookieNameToRemove;
	}

	@Override
	public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
			@NonNull ClientHttpRequestExecution execution)
			throws IOException {
		List<String> cookies = request.getHeaders().get("Cookie");

		if (cookies != null) {
			List<String> filteredCookies = cookies.stream()
					.flatMap(cookieHeader -> List.of(cookieHeader != null ? cookieHeader.split(";") : new String[0]).stream())
					.filter(cookie -> !cookie.trim().startsWith(cookieNameToRemove + "="))
					.collect(Collectors.toList());

			request.getHeaders().remove("Cookie");
			if (!filteredCookies.isEmpty()) {
				request.getHeaders().add("Cookie", String.join("; ", filteredCookies));
			}
		}

		return execution.execute(request, body);
	}
}
