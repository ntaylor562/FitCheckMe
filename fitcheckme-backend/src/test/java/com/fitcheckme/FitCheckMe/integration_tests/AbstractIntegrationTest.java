package com.fitcheckme.FitCheckMe.integration_tests;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
	@ServiceConnection
	protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@Autowired
	protected TestRestTemplate restTemplate;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected static final ObjectMapper mapper = new ObjectMapper();

	private static String accessTokenCookie;
	private static String refreshTokenCookie;

	static {
		postgres.start();
	}

	@BeforeAll
	protected void setup() {
		postgres.start();

		Resource resource = new ClassPathResource("data-test.sql");
		try {
			// Populate DB with sample data in data sql file
			jdbcTemplate.execute(FileCopyUtils
					.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new RuntimeException(String.format("ERROR reading file for inital test data: %s", e.toString()));
		}

		// Logging in as a test user and setting cookies for the rest template
		// TODO get the tokens from the cookies instead of the response body in case
		// they're removed from response body
		UserLoginReturnDTO result = getObjectFromResponse(
				postCall("/api/auth/login",
						new UserLoginRequestDTO("test", "test")),
				UserLoginReturnDTO.class);

		AbstractIntegrationTest.accessTokenCookie = "jwt-access-token=" + result.accessToken()
				+ "; Path=/; Secure; HttpOnly;";
		AbstractIntegrationTest.refreshTokenCookie = "jwt-refresh-token=" + result.refreshToken()
				+ "; Path=/api/auth; Secure; HttpOnly;";
	}

	@BeforeEach
	protected void addAuthTokensToRestTemplate() {
		restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
			request.getHeaders().add("Cookie", AbstractIntegrationTest.accessTokenCookie);
			request.getHeaders().add("Cookie", AbstractIntegrationTest.refreshTokenCookie);
			return execution.execute(request, body);
		});
	}

	protected static <T> ParameterizedTypeReference<List<T>> getTypeOfListOfType(Class<T> classType) {
		return new ParameterizedTypeReference<List<T>>() {
		};
	}

	public <T> ResponseEntity<T> restCall(String url, HttpMethod method, HttpEntity<T> requestEntity) {
		ResponseEntity<T> res = restTemplate.exchange(url, method, requestEntity, new ParameterizedTypeReference<T>() {
		});
		if (res.getStatusCode().isError()) {
			throw new RuntimeException("Error in rest call: " + res.toString());
		}

		return res;
	}

	public ResponseEntity<Object> restCall(String url, HttpMethod method) {
		return restCall(url, method, null);
	}

	public ResponseEntity<Object> getCall(String url) {
		return restCall(url, HttpMethod.GET);
	}

	public <T> ResponseEntity<T> postCall(String url, T body) {
		return restCall(url, HttpMethod.POST, new HttpEntity<T>(body));
	}

	public <T> T getObjectFromResponse(ResponseEntity<Object> response, Class<T> classType) {
		return mapper.convertValue(Optional.of(response.getBody()).get(), classType);
	}

	public <T> List<T> getListOfObjectsFromResponse(ResponseEntity<Object> response, Class<T> classType) {
		return mapper.convertValue(Optional.of(response.getBody()).get(), new TypeReference<List<T>>() {
		}).stream().map(tag -> mapper.convertValue(tag, classType)).toList();
	}

}
