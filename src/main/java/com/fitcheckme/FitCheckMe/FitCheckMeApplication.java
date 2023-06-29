package com.fitcheckme.FitCheckMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//TODO Add tests
//TODO Update controllers with better error handling, especially for the DB errors
@SpringBootApplication
@EnableTransactionManagement
@PropertySource("classpath:/.env")
public class FitCheckMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitCheckMeApplication.class, args);
	}

}
