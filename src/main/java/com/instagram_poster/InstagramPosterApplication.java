package com.instagram_poster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InstagramPosterApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstagramPosterApplication.class, args);
	}

}
