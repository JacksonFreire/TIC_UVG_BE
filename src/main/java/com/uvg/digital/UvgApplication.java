package com.uvg.digital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UvgApplication {

	public static void main(String[] args) {
		SpringApplication.run(UvgApplication.class, args);
	}
}
