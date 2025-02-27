package com.sina_reidenbach.insurancePremium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InsurancePremiumApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(InsurancePremiumApplication.class);
		app.setAdditionalProfiles("prod");
		app.run(args);
	}
}