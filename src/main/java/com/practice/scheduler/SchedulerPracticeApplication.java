package com.practice.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SchedulerPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerPracticeApplication.class, args);
	}

}
