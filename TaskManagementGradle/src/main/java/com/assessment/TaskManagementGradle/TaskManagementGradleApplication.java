package com.assessment.TaskManagementGradle;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Key;

@SpringBootApplication
public class TaskManagementGradleApplication {

	public static void main(String[] args) {

		SpringApplication.run(TaskManagementGradleApplication.class, args);
//		Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
//		String base64Key = Encoders.BASE64.encode(key.getEncoded());
//		System.out.println("Generated Secret Key: " + base64Key);

//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		System.out.println("Encoded password: " + encoder.encode("123"));

	}

}
