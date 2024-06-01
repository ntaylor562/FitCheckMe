package com.fitcheckme.FitCheckMe.integration_tests;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.ExceptionResponseDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;
import com.fitcheckme.FitCheckMe.repositories.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Rollback
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
	@ServiceConnection
	protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@Autowired
	protected TestRestTemplate restTemplate;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	private UserRepository userRepository;

	protected static final ObjectMapper mapper = new ObjectMapper();

	private static final String defaultUsername = "test_user";
	private String currentUsername;
	private String accessToken;
	private String refreshToken;

	static {
		postgres.start();
	}

	protected String getAccessToken() {
		return this.accessToken;
	}

	protected String getRefreshToken() {
		return this.refreshToken;
	}

	@BeforeAll
	protected void setupAll() {
		Resource resource = new ClassPathResource("data-test.sql");
		if (userRepository.count() == 0) {
			try {
				// Populate DB with sample data in data sql file
				jdbcTemplate.execute(FileCopyUtils
						.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
			} catch (Exception e) {
				throw new RuntimeException(String.format("ERROR reading file for inital test data: %s", e.toString()));
			}
		}
	}

	protected void resetAuth() {
		if(this.accessToken != null && this.refreshToken != null) {
			// For some reason, tokens are removed after each test so we add them back here
			addAuthTokensToRestTemplate(this.accessToken, this.refreshToken);
		}

		if (!AbstractIntegrationTest.defaultUsername.equals(this.currentUsername)) {
			logout();
			login(AbstractIntegrationTest.defaultUsername);
		}
	}

	protected void addAuthTokensToRestTemplate(String accessToken, String refreshToken) {
		String accessTokenCookie = "jwt-access-token=" + accessToken + "; Path=/; Secure; HttpOnly;";
		String refreshTokenCookie = "jwt-refresh-token=" + refreshToken + "; Path=/api/auth; Secure; HttpOnly;";
		restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().add("Cookie", accessTokenCookie);
			request.getHeaders().add("Cookie", refreshTokenCookie);
			return execution.execute(request, body);
		});
	}

	protected void removeAuthTokensFromRestTemplate() {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(
				restTemplate.getRestTemplate().getInterceptors());
		interceptors.add(new RemoveCookieInterceptor("jwt-access-token"));
		interceptors.add(new RemoveCookieInterceptor("jwt-refresh-token"));
		restTemplate.getRestTemplate().setInterceptors(interceptors);
	}

	protected void login(String username, String password) {
		// TODO get the tokens from the cookies instead of the response body in case
		// they're removed from response body
		UserLoginReturnDTO result = getObjectFromResponse(
				postCall("/api/auth/login",
						new UserLoginRequestDTO(username, password)),
				UserLoginReturnDTO.class);

		this.currentUsername = username;
		this.accessToken = result.accessToken();
		this.refreshToken = result.refreshToken();
		addAuthTokensToRestTemplate(this.accessToken, this.refreshToken);
	}

	protected void login(String username) {
		login(username, "test");
	}

	protected void logout() {
		if (this.currentUsername == null) {
			return;
		}
		restTemplate.postForEntity("/api/auth/logout", null, Object.class);
		removeAuthTokensFromRestTemplate();
		this.currentUsername = null;
		this.accessToken = null;
		this.refreshToken = null;
	}

	protected static <T> ParameterizedTypeReference<List<T>> getTypeOfListOfType(Class<T> classType) {
		return new ParameterizedTypeReference<List<T>>() {
		};
	}

	protected <T> ResponseEntity<T> restCall(String url, HttpMethod method, HttpEntity<T> requestEntity,
			boolean expectError) {
		ResponseEntity<T> response = restTemplate.exchange(url, method, requestEntity,
				new ParameterizedTypeReference<T>() {
				});
		if (!expectError && response.getStatusCode().isError()) {
			throw new RuntimeException(String.format("ERROR in response: %s", response.toString()));
		} else if (expectError && !response.getStatusCode().isError()) {
			throw new RuntimeException(
					String.format("Expected error but found none in response: %s", response.toString()));
		}

		return response;
	}

	protected ResponseEntity<Object> restCall(String url, HttpMethod method) {
		return restCall(url, method, null, false);
	}

	protected ResponseEntity<Object> getCall(String url) {
		return restCall(url, HttpMethod.GET);
	}

	protected ResponseEntity<Object> getCall(String url, boolean expectError) {
		return restCall(url, HttpMethod.GET, null, expectError);
	}

	protected <T> ResponseEntity<T> postCall(String url, T body) {
		return restCall(url, HttpMethod.POST, new HttpEntity<T>(body), false);
	}

	protected <T> ResponseEntity<T> postCall(String url, T body, boolean expectError) {
		return restCall(url, HttpMethod.POST, new HttpEntity<T>(body), expectError);
	}

	protected <T> ResponseEntity<T> putCall(String url, T body) {
		return restCall(url, HttpMethod.PUT, new HttpEntity<T>(body), false);
	}

	protected <T> ResponseEntity<T> putCall(String url, T body, boolean expectError) {
		return restCall(url, HttpMethod.PUT, new HttpEntity<T>(body), expectError);
	}

	protected ResponseEntity<Object> deleteCall(String url) {
		return restCall(url, HttpMethod.DELETE);
	}

	protected ResponseEntity<Object> deleteCall(String url, boolean expectError) {
		return restCall(url, HttpMethod.DELETE, null, expectError);
	}

	protected <T> T getObjectFromResponse(ResponseEntity<Object> response, Class<T> classType) {
		return mapper.convertValue(Optional.of(response.getBody()).get(), classType);
	}

	protected <T> List<T> getListOfObjectsFromResponse(ResponseEntity<Object> response, Class<T> classType) {
		return mapper.convertValue(Optional.of(response.getBody()).get(), new TypeReference<List<T>>() {
		}).stream().map(tag -> mapper.convertValue(tag, classType)).toList();
	}

	protected ExceptionResponseDTO getExceptionResponseFromResponse(ResponseEntity<Object> response) {
		return getObjectFromResponse(response, ExceptionResponseDTO.class);
	}

}
