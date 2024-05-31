package com.fitcheckme.FitCheckMe.integration_tests;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
	private String accessTokenCookie;
	private String refreshTokenCookie;

	static {
		postgres.start();
	}

	@BeforeAll
	protected void setup() {
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

		login(AbstractIntegrationTest.defaultUsername);
	}

	@BeforeEach
	protected void addAuthTokensToRestTemplate() {
		restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().add("Cookie", this.accessTokenCookie);
			request.getHeaders().add("Cookie", this.refreshTokenCookie);
			return execution.execute(request, body);
		});
	}

	@AfterEach
	protected void resetAuth() {
		if(!AbstractIntegrationTest.defaultUsername.equals(currentUsername)) {
			logout();
			login(AbstractIntegrationTest.defaultUsername);
		}
	}

	protected void removeAuthTokensFromRestTemplate() {
		restTemplate.getRestTemplate().getInterceptors().removeIf(interceptor -> {
			return interceptor.toString().contains("jwt-access-token")
					|| interceptor.toString().contains("jwt-refresh-token");
		});
	}

	protected void login(String username) {
		// TODO get the tokens from the cookies instead of the response body in case
		// they're removed from response body
		UserLoginReturnDTO result = getObjectFromResponse(
				postCall("/api/auth/login",
						new UserLoginRequestDTO(username, "test")),
				UserLoginReturnDTO.class);

		this.currentUsername = username;
		this.accessTokenCookie = "jwt-access-token=" + result.accessToken()
				+ "; Path=/; Secure; HttpOnly;";
		this.refreshTokenCookie = "jwt-refresh-token=" + result.refreshToken()
				+ "; Path=/api/auth; Secure; HttpOnly;";
		addAuthTokensToRestTemplate();
	}

	protected void logout() {
		if(this.currentUsername == null) {
			return;
		}
		restTemplate.postForEntity("/api/auth/logout", null, Object.class);
		removeAuthTokensFromRestTemplate();
		this.currentUsername = null;
		this.accessTokenCookie = null;
		this.refreshTokenCookie = null;
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
		}
		else if(expectError && !response.getStatusCode().isError()) {
			throw new RuntimeException(String.format("Expected error but found none in response: %s", response.toString()));
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

	protected <T> ResponseEntity<T> postCall(String url) {
		return restCall(url, HttpMethod.POST, null, false);
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
