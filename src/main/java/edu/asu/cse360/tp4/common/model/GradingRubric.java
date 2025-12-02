package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Grading rubric model for instructor-defined assessment criteria.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class GradingRubric {
    
    private final String id;
    private final String name;
    private final String instructorId;
    private final Map<String, Double> criteria;
    private final LocalDateTime createdAt;
    
    public GradingRubric(String id, String name, String instructorId, 
                        Map<String, Double> criteria, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.instructorId = instructorId;
        this.criteria = criteria;
        this.createdAt = createdAt;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getInstructorId() { return instructorId; }
    public Map<String, Double> getCriteria() { return criteria; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}