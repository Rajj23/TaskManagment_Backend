package com.taskManagment.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskManagmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagmentApplication.class, args);
	}

}
