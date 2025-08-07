package com.edu.tutor_platform;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TutorConnect {



	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		String dbUsername = dotenv.get("DB_USERNAME");
		String dbPassword = dotenv.get("DB_PASSWORD");

		if (dbUsername != null && dbPassword != null) {
			System.setProperty("spring.datasource.username", dbUsername);
			System.setProperty("spring.datasource.password", dbPassword);
		} else {
			System.err.println("Missing DB_USERNAME or DB_PASSWORD environment variable. Exiting...");
			System.exit(1);
		}

		System.setProperty("spring.datasource.url", "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres");
		System.setProperty("spring.datasource.username", dotenv.get("DB_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));

		SpringApplication.run(TutorConnect.class, args);
	}
}
