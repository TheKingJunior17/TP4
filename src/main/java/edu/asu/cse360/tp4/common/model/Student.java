package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;

/**
 * Student entity model for the TP4 system.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class Student {
    
    private final String id;
    private final String name;
    private final String email;
    private final LocalDateTime enrollmentDate;
    
    public Student(String id, String name, String email, LocalDateTime enrollmentDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
}