package com.fitcheckme.FitCheckMe;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class FitCheckMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitCheckMeApplication.class, args);
	}

}
