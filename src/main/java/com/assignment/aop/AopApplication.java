package com.assignment.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Main entry point for the AOP Data Synchronization Microservice.
 * <p>
 * This application demonstrates a "Dual-Write" architecture, utilizing Aspect-Oriented
 * Programming (AOP) to seamlessly synchronize data between a primary relational database
 * and a secondary NoSQL datastore without tightly coupling the persistence logic.
 * </p>
 *
 * <h3>Key Implementations & Features:</h3>
 * <ul>
 * <li><b>Spring AOP:</b> Utilizes {@code @AfterReturning} advice to intercept JPA save operations
 * and trigger secondary data flows.</li>
 * <li><b>Adapter Design Pattern:</b> Implements a generic interface to transform relational
 * SQL entities ({@code UserEntity}) into NoSQL documents ({@code UserDoc}).</li>
 * <li><b>Dual-Database Integration:
 * </b> Configured to support both Spring Data JPA (MySQL/SQL Server)
 * and Spring Data MongoDB concurrently.</li>
 * <li><b>RESTful API Design:</b>
 * Features a thin Controller layer that delegates business logic to
 * Services and uses DTOs (Data Transfer Objects) for clean API contracts.</li>
 * <li><b>Lombok Integration:</b>
 * Eliminates boilerplate code across all models using {@code @Builder},
 * {@code @Data}, and constructor generation.</li>
 * <li><b>Code Quality Enforcement:</b>
 * Integrated the Maven Checkstyle Plugin
 * using the Google Java Style
 * configuration to maintain strict,
 * industry-standard code formatting across the project.</li>
 * <li><b>Pure Unit Testing:</b>
 * Achieves high test coverage using JUnit 5 and Mockito ({@code @Mock},
 * {@code @InjectMocks}) to test layers in total isolation without loading the Spring Context.</li>
 * <li><b>Professional Documentation:</b> Applies industry-standard JavaDoc formatting across all
 * layers (Controllers, Services, Aspects, Adapters, and Models) for peer review readiness.</li>
 * </ul>
 *
 * @author Ansh Parnami
 * @version 1.0
 */

@SpringBootApplication

public class AopApplication {

  public static void main(String[] args) {
    SpringApplication.run(AopApplication.class, args);
  }
}
