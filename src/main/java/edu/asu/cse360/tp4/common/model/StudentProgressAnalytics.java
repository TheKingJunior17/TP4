package edu.asu.cse360.tp4.common.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive student progress analytics data structure.
 * 
 * Contains all metrics, trends, and insights related to a student's
 * academic performance and engagement in the TP4 system.
 * 
 * @author TP4 Team
 * @version 2.0.0
 * @since TP4 Implementation Phase
 */
public class StudentProgressAnalytics {
    
    private final String studentId;
    private final String studentName;
    private final double overallGrade;
    private final double engagementScore;
    private final double progressVelocity;
    private final RiskLevel riskLevel;
    private final List<String> recommendations;
    private final List<PerformanceTrend> trends;
    private final Map<String, Double> peerComparisons;
    private final Instant generatedAt;
    
    public StudentProgressAnalytics(String studentId, String studentName, 
                                  double overallGrade, double engagementScore, 
                                  double progressVelocity, RiskLevel riskLevel,
                                  List<String> recommendations, 
                                  List<PerformanceTrend> trends,
                                  Map<String, Double> peerComparisons,
                                  Instant generatedAt) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.overallGrade = overallGrade;
        this.engagementScore = engagementScore;
        this.progressVelocity = progressVelocity;
        this.riskLevel = riskLevel;
        this.recommendations = recommendations;
        this.trends = trends;
        this.peerComparisons = peerComparisons;
        this.generatedAt = generatedAt;
    }
    
    // Getters
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public double getOverallGrade() { return overallGrade; }
    public double getEngagementScore() { return engagementScore; }
    public double getProgressVelocity() { return progressVelocity; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getRecommendations() { return recommendations; }
    public List<PerformanceTrend> getTrends() { return trends; }
    public Map<String, Double> getPeerComparisons() { return peerComparisons; }
    public Instant getGeneratedAt() { return generatedAt; }
    
    // Helper class for trends
    public static class PerformanceTrend {
        private final String metric;
        private final TrendDirection direction;
        private final double changeValue;
        
        public PerformanceTrend(String metric, TrendDirection direction, double changeValue) {
            this.metric = metric;
            this.direction = direction;
            this.changeValue = changeValue;
        }
        
        public String getMetric() { return metric; }
        public TrendDirection getDirection() { return direction; }
        public double getChangeValue() { return changeValue; }
    }
    
    public enum RiskLevel { LOW, MODERATE, HIGH, CRITICAL }
    public enum TrendDirection { IMPROVING, STABLE, DECLINING }
}