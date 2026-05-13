package com.projek.hr_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HrBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrBackendApplication.class, args);
	}

}
