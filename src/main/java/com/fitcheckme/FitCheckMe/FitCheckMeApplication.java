package com.fitcheckme.FitCheckMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@PropertySource("classpath:/.env")
public class FitCheckMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitCheckMeApplication.class, args);
	}

}
