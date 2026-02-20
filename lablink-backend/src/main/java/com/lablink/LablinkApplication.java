package com.lablink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LabLink Backend Application
 *
 * Entry point for the Spring Boot API server.
 * Serves both the React web dashboard and Android mobile client.
 */
@SpringBootApplication
public class LablinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(LablinkApplication.class, args);
    }
}
