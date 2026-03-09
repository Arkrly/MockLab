package io.mocklab.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MockLabApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockLabApiApplication.class, args);
	}

}
