package com.f1rst.challenge.address_finder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableFeignClients
public class AddressFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddressFinderApplication.class, args);
	}

}
