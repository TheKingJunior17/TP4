package edu.asu.cse360.tp4.common.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Class performance analytics data structure for instructors.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class ClassPerformanceAnalytics {
    
    private final String classId;
    private final String instructorId;
    private final int studentCount;
    private final double classAverage;
    private final Map<String, Double> gradeDistribution;
    private final List<String> atRiskStudents;
    private final Map<String, Object> engagementMetrics;
    private final List<Object> trends;
    private final Map<String, Map<String, Object>> individualAnalytics;
    private final Instant generatedAt;
    
    public ClassPerformanceAnalytics(String classId, String instructorId, int studentCount,
                                   double classAverage, Map<String, Double> gradeDistribution,
                                   List<String> atRiskStudents, Map<String, Object> engagementMetrics,
                                   List<Object> trends, Map<String, Map<String, Object>> individualAnalytics,
                                   Instant generatedAt) {
        this.classId = classId;
        this.instructorId = instructorId;
        this.studentCount = studentCount;
        this.classAverage = classAverage;
        this.gradeDistribution = gradeDistribution;
        this.atRiskStudents = atRiskStudents;
        this.engagementMetrics = engagementMetrics;
        this.trends = trends;
        this.individualAnalytics = individualAnalytics;
        this.generatedAt = generatedAt;
    }
    
    // Getters
    public String getClassId() { return classId; }
    public String getInstructorId() { return instructorId; }
    public int getStudentCount() { return studentCount; }
    public double getClassAverage() { return classAverage; }
    public Map<String, Double> getGradeDistribution() { return gradeDistribution; }
    public List<String> getAtRiskStudents() { return atRiskStudents; }
    public Map<String, Object> getEngagementMetrics() { return engagementMetrics; }
    public List<Object> getTrends() { return trends; }
    public Map<String, Map<String, Object>> getIndividualAnalytics() { return individualAnalytics; }
    public Instant getGeneratedAt() { return generatedAt; }
}