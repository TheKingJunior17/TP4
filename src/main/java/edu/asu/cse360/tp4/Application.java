package edu.asu.cse360.tp4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for TP4 Multi-Role Educational Platform.
 * 
 * This Spring Boot application provides a comprehensive educational platform
 * supporting multiple user roles (Student, Instructor, Staff) with integrated
 * authentication, analytics, and collaboration features.
 * 
 * <p>Key Features:</p>
 * <ul>
 *   <li>Multi-role authentication with MFA support</li>
 *   <li>Real-time analytics and progress tracking</li>
 *   <li>Comprehensive grading and assessment workflows</li>
 *   <li>Administrative oversight and reporting</li>
 *   <li>Enterprise-grade security and audit logging</li>
 * </ul>
 * 
 * @author TP4 Team - CSE 360
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
public class Application {

    /**
     * Main method to start the TP4 Multi-Role Educational Platform.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}