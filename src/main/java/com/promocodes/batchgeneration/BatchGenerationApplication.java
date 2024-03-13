package com.promocodes.batchgeneration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.promocodes.batchgeneration.config", "com.promocodes.batchgeneration.controller", "com.promocodes.batchgeneration.kafka"})
public class BatchGenerationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchGenerationApplication.class, args);
	}

}
