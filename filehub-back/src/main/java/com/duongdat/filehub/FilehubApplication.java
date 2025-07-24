package com.duongdat.filehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class FilehubApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilehubApplication.class, args);
	}

}

// Note: UserService will be added in separate file