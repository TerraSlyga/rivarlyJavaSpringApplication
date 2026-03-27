package com.example.rivarly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The main entry point for the Rivarly application.
 * This class boots up the Spring Boot application.
 */
@SpringBootApplication
@EnableCaching
public class RivarlyApplication {

    /**
     * The main method to launch the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RivarlyApplication.class, args);
    }

}