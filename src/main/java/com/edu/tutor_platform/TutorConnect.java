// package com.edu.tutor_platform;

// import io.github.cdimascio.dotenv.Dotenv;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class TutorConnect {



// 	public static void main(String[] args) {
// 		Dotenv dotenv = Dotenv.load();
// 		String dbUsername = dotenv.get("DB_USERNAME");
// 		String dbPassword = dotenv.get("DB_PASSWORD");
// 		String dbUrl = dotenv.get("DB_URL");

// 		if (dbUsername != null && dbPassword != null && dbUrl != null) {
// 			System.setProperty("spring.datasource.url", dbUrl);
// 			System.setProperty("spring.datasource.username", dbUsername);
// 			System.setProperty("spring.datasource.password", dbPassword);
// 		} else {
// 			System.err.println("Missing DB_USERNAME or DB_PASSWORD environment variable. Exiting...");
// 			System.exit(1);
// 		}


// 		SpringApplication.run(TutorConnect.class, args);
// 	}
// }
