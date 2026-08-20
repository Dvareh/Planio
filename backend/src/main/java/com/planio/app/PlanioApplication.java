package com.planio.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlanioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanioApplication.class, args);
	}

}
