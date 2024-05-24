package com.fitcheckme.FitCheckMe.integration_tests;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginRequestDTO;
import com.fitcheckme.FitCheckMe.DTOs.auth.UserLoginReturnDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
	@Container
	@ServiceConnection
	protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@Autowired
	protected TestRestTemplate restTemplate;

	protected static final ObjectMapper mapper = new ObjectMapper();

	protected static <T> ParameterizedTypeReference<List<T>> getTypeOfListOfType(Class<T> classType) {
		return new ParameterizedTypeReference<List<T>>() {
		};
	}

	static {
		postgres.start();
	}

	@BeforeAll
	protected void setup() {
		// Logging in as a test user and setting cookies for the rest template
		//TODO get the tokens from the cookies instead of the response body in case they're removed from response body
		UserLoginReturnDTO result = Optional.of(
				restTemplate.exchange(
						"/api/auth/login",
						HttpMethod.POST,
						new HttpEntity<>(new UserLoginRequestDTO("test", "test")),
						UserLoginReturnDTO.class)
						.getBody())
				.orElseThrow(() -> new AccessDeniedException("Unable to log in for integration tests"));

		restTemplate.getRestTemplate().getInterceptors().add(((request, body, execution) -> {
			request.getHeaders().add("Cookie",
					"jwt-access-token=" + result.accessToken() + "; Path=/; Secure; HttpOnly;");
			request.getHeaders().add("Cookie",
					"jwt-refresh-token=" + result.refreshToken() + "; Path=/api/auth; Secure; HttpOnly;");
			return execution.execute(request, body);
		}));
	}

}
