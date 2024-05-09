package com.fitcheckme.FitCheckMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//TODO Update models to use sets instead of lists where appropriate
//TODO Add tests
//TODO Update controllers with better error handling, especially for the DB errors
//TODO Ensure that before any DB checks for things like username, make checks on length and everything first so there's no unnecessary querying
//TODO Find a way to auto delete expired refresh tokens
@SpringBootApplication
@EnableTransactionManagement
@PropertySource("classpath:/.env")
public class FitCheckMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitCheckMeApplication.class, args);
	}

}
