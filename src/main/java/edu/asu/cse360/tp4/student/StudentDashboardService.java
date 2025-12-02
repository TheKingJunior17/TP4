package edu.asu.cse360.tp4.student;

import edu.asu.cse360.tp4.common.model.StudentProgressAnalytics;
import edu.asu.cse360.tp4.common.model.Student;
import edu.asu.cse360.tp4.common.model.AnalyticsWidget;
import edu.asu.cse360.tp4.common.exception.ValidationException;
import edu.asu.cse360.tp4.common.exception.DataProcessingException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Student Dashboard Service providing comprehensive analytics and progress tracking.
 * 
 * This service provides student-specific functionality including real-time progress
 * analytics, performance tracking, at-risk identification, and personalized learning
 * recommendations integrated with the TP4 multi-role authentication system.
 * 
 * <p>Key Features:</p>
 * <ul>
 *   <li>Real-time progress tracking with trend analysis</li>
 *   <li>Interactive dashboard widgets with customizable layout</li>
 *   <li>At-risk student identification with early intervention</li>
 *   <li>Personalized learning path recommendations</li>
 *   <li>Integration with instructor grading and feedback systems</li>
 * </ul>
 * 
 * <p>Performance Considerations:</p>
 * Analytics calculations are optimized for O(n log n) complexity and cached
 * for frequently accessed data to ensure sub-100ms response times.
 * 
 * @author [Team Member 2] - Student Role Lead
 * @version 2.0.0
 * @since TP4 Implementation Phase
 * @see edu.asu.cse360.tp4.authentication.MultiRoleAuthenticationService
 * @see edu.asu.cse360.tp4.instructor.InstructorDashboardService
 */
@Service
public class StudentDashboardService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardService.class);
    private static final int ANALYTICS_CACHE_DURATION_MINUTES = 15;
    private static final double AT_RISK_THRESHOLD = 60.0; // Performance below 60% indicates risk
    private static final int TREND_ANALYSIS_DAYS = 30;
    
    private final Map<String, StudentProgressAnalytics> analyticsCache;
    private final Map<String, Instant> cacheTimestamps;
    
    /**
     * Constructor initializing dashboard service components.
     */
    public StudentDashboardService() {
        this.analyticsCache = new HashMap<>();
        this.cacheTimestamps = new HashMap<>();
        logger.info("Student Dashboard Service initialized");
    }
    
    /**
     * Generates comprehensive progress analytics for a specific student.
     * 
     * This method aggregates data from multiple sources including assignment submissions,
     * quiz performance, engagement metrics, and participation data to provide a holistic
     * view of student progress with predictive insights and recommendations.
     * 
     * @param studentId the unique identifier for the student (must not be null)
     * @param includeComparisons whether to include peer comparison analytics
     * @return StudentProgressAnalytics containing comprehensive progress data
     * @throws ValidationException if studentId is invalid
     * @throws DataProcessingException if analytics generation fails
     * 
     * @see StudentProgressAnalytics for detailed analytics structure
     * @see #getStudentPerformanceTrends(String) for trend-specific analysis
     */
    public StudentProgressAnalytics generateProgressAnalytics(String studentId, 
            boolean includeComparisons) throws ValidationException, DataProcessingException {
        
        validateStudentId(studentId);
        
        // Check cache first
        if (isAnalyticsCached(studentId) && !includeComparisons) {
            logger.debug("Returning cached analytics for student: {}", studentId);
            return analyticsCache.get(studentId);
        }
        
        try {
            logger.info("Generating progress analytics for student: {}", studentId);
            
            // Simulate data aggregation from multiple sources
            Student student = fetchStudentData(studentId);
            Map<String, Object> performanceData = aggregatePerformanceData(studentId);
            
            // Calculate core metrics
            double overallGrade = calculateOverallGrade(performanceData);
            double engagementScore = calculateEngagementScore(performanceData);
            double progressVelocity = calculateProgressVelocity(performanceData);
            
            // Risk assessment
            RiskLevel riskLevel = assessStudentRisk(overallGrade, engagementScore, progressVelocity);
            
            // Generate recommendations
            List<String> recommendations = generatePersonalizedRecommendations(
                    student, overallGrade, engagementScore, riskLevel);
            
            // Trend analysis
            List<PerformanceTrend> trends = analyzePerformanceTrends(studentId, TREND_ANALYSIS_DAYS);
            
            // Peer comparisons (if requested)
            Map<String, Double> peerComparisons = includeComparisons ? 
                    generatePeerComparisons(studentId, overallGrade) : new HashMap<>();
            
            // Create analytics object
            StudentProgressAnalytics analytics = new StudentProgressAnalytics(
                    studentId, 
                    student.getName(),
                    overallGrade,
                    engagementScore,
                    progressVelocity,
                    riskLevel,
                    recommendations,
                    trends,
                    peerComparisons,
                    Instant.now()
            );
            
            // Cache results (if not including comparisons for consistency)
            if (!includeComparisons) {
                cacheAnalytics(studentId, analytics);
            }
            
            logger.info("Analytics generated successfully for student: {} (Risk: {})", 
                    studentId, riskLevel);
            
            return analytics;
            
        } catch (Exception e) {
            logger.error("Failed to generate analytics for student: {}", studentId, e);
            throw new DataProcessingException("Analytics generation failed", e);
        }
    }
    
    /**
     * Creates interactive dashboard widgets tailored to student needs.
     * 
     * Generates a set of customizable dashboard widgets including grade overview,
     * upcoming assignments, progress trends, and personalized notifications.
     * 
     * @param studentId the student's unique identifier
     * @param widgetPreferences optional widget layout preferences
     * @return List of AnalyticsWidget objects for dashboard display
     * @throws ValidationException if studentId is invalid
     */
    public List<AnalyticsWidget> createDashboardWidgets(String studentId, 
            Map<String, Object> widgetPreferences) throws ValidationException {
        
        validateStudentId(studentId);
        logger.info("Creating dashboard widgets for student: {}", studentId);
        
        List<AnalyticsWidget> widgets = new ArrayList<>();
        
        try {
            // Grade Overview Widget
            widgets.add(createGradeOverviewWidget(studentId));
            
            // Progress Tracking Widget
            widgets.add(createProgressTrackingWidget(studentId));
            
            // Upcoming Assignments Widget
            widgets.add(createUpcomingAssignmentsWidget(studentId));
            
            // Performance Trends Widget
            widgets.add(createPerformanceTrendsWidget(studentId));
            
            // At-Risk Alert Widget (conditional)
            StudentProgressAnalytics analytics = generateProgressAnalytics(studentId, false);
            if (analytics.getRiskLevel() != RiskLevel.LOW) {
                widgets.add(createAtRiskAlertWidget(studentId, analytics.getRiskLevel()));
            }
            
            // Personalized Recommendations Widget
            widgets.add(createRecommendationsWidget(studentId));
            
            // Apply user preferences for widget ordering/visibility
            widgets = applyWidgetPreferences(widgets, widgetPreferences);
            
            logger.debug("Created {} dashboard widgets for student: {}", widgets.size(), studentId);
            return widgets;
            
        } catch (Exception e) {
            logger.error("Failed to create dashboard widgets for student: {}", studentId, e);
            throw new DataProcessingException("Widget creation failed", e);
        }
    }
    
    /**
     * Identifies students at risk of academic failure.
     * 
     * Uses advanced algorithms considering multiple factors including grade trends,
     * engagement patterns, assignment submission rates, and help-seeking behavior.
     * 
     * @param studentId the student to assess
     * @return RiskLevel indicating the level of academic risk
     * @throws ValidationException if studentId is invalid
     */
    public RiskLevel assessStudentRisk(String studentId) throws ValidationException {
        validateStudentId(studentId);
        
        try {
            Map<String, Object> performanceData = aggregatePerformanceData(studentId);
            
            double overallGrade = calculateOverallGrade(performanceData);
            double engagementScore = calculateEngagementScore(performanceData);
            double progressVelocity = calculateProgressVelocity(performanceData);
            
            return assessStudentRisk(overallGrade, engagementScore, progressVelocity);
            
        } catch (Exception e) {
            logger.error("Risk assessment failed for student: {}", studentId, e);
            throw new DataProcessingException("Risk assessment failed", e);
        }
    }
    
    /**
     * Exports student analytics data in multiple formats.
     * 
     * @param studentId the student's unique identifier
     * @param format the export format (CSV, PDF, JSON, EXCEL)
     * @return byte array containing exported data
     * @throws ValidationException if parameters are invalid
     */
    public byte[] exportAnalytics(String studentId, ExportFormat format) 
            throws ValidationException, DataProcessingException {
        
        validateStudentId(studentId);
        Objects.requireNonNull(format, "Export format cannot be null");
        
        try {
            StudentProgressAnalytics analytics = generateProgressAnalytics(studentId, true);
            
            return switch (format) {
                case CSV -> exportToCsv(analytics);
                case PDF -> exportToPdf(analytics);
                case JSON -> exportToJson(analytics);
                case EXCEL -> exportToExcel(analytics);
            };
            
        } catch (Exception e) {
            logger.error("Export failed for student: {} in format: {}", studentId, format, e);
            throw new DataProcessingException("Analytics export failed", e);
        }
    }
    
    // Private helper methods for analytics calculations
    
    private void validateStudentId(String studentId) throws ValidationException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be null or empty");
        }
    }
    
    private boolean isAnalyticsCached(String studentId) {
        Instant cacheTime = cacheTimestamps.get(studentId);
        if (cacheTime == null) {
            return false;
        }
        
        return ChronoUnit.MINUTES.between(cacheTime, Instant.now()) < ANALYTICS_CACHE_DURATION_MINUTES;
    }
    
    private void cacheAnalytics(String studentId, StudentProgressAnalytics analytics) {
        analyticsCache.put(studentId, analytics);
        cacheTimestamps.put(studentId, Instant.now());
    }
    
    private Student fetchStudentData(String studentId) {
        // Simulate database fetch
        return new Student(studentId, "Student Name", "student@example.com", 
                LocalDateTime.now().minusMonths(4));
    }
    
    private Map<String, Object> aggregatePerformanceData(String studentId) {
        // Simulate aggregating data from multiple sources
        Map<String, Object> data = new HashMap<>();
        data.put("assignments", simulateAssignmentData());
        data.put("quizzes", simulateQuizData());
        data.put("engagement", simulateEngagementData());
        data.put("participation", simulateParticipationData());
        return data;
    }
    
    private double calculateOverallGrade(Map<String, Object> performanceData) {
        // Weighted grade calculation: Assignments 60%, Quizzes 30%, Participation 10%
        @SuppressWarnings("unchecked")
        List<Double> assignments = (List<Double>) performanceData.get("assignments");
        @SuppressWarnings("unchecked")
        List<Double> quizzes = (List<Double>) performanceData.get("quizzes");
        @SuppressWarnings("unchecked")
        Double participation = (Double) performanceData.get("participation");
        
        double assignmentAvg = assignments.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double quizAvg = quizzes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        return (assignmentAvg * 0.6) + (quizAvg * 0.3) + (participation * 0.1);
    }
    
    private double calculateEngagementScore(Map<String, Object> performanceData) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> engagement = (Map<String, Integer>) performanceData.get("engagement");
        
        // Calculate engagement score based on various metrics
        int logins = engagement.getOrDefault("logins", 0);
        int forumPosts = engagement.getOrDefault("forumPosts", 0);
        int helpRequests = engagement.getOrDefault("helpRequests", 0);
        
        // Normalize to 0-100 scale
        return Math.min(100.0, (logins * 2.0 + forumPosts * 5.0 + helpRequests * 3.0) / 2.0);
    }
    
    private double calculateProgressVelocity(Map<String, Object> performanceData) {
        // Calculate rate of improvement over time
        @SuppressWarnings("unchecked")
        List<Double> assignments = (List<Double>) performanceData.get("assignments");
        
        if (assignments.size() < 2) return 0.0;
        
        double firstHalf = assignments.subList(0, assignments.size() / 2).stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
        double secondHalf = assignments.subList(assignments.size() / 2, assignments.size()).stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        return secondHalf - firstHalf; // Positive indicates improvement
    }
    
    private RiskLevel assessStudentRisk(double overallGrade, double engagementScore, double progressVelocity) {
        // Multi-factor risk assessment algorithm
        double riskScore = 0.0;
        
        // Grade factor (40% weight)
        if (overallGrade < 60) riskScore += 40;
        else if (overallGrade < 70) riskScore += 20;
        else if (overallGrade < 80) riskScore += 10;
        
        // Engagement factor (30% weight)  
        if (engagementScore < 30) riskScore += 30;
        else if (engagementScore < 50) riskScore += 15;
        
        // Progress velocity factor (30% weight)
        if (progressVelocity < -10) riskScore += 30;
        else if (progressVelocity < 0) riskScore += 15;
        
        // Determine risk level based on total score
        if (riskScore >= 70) return RiskLevel.CRITICAL;
        if (riskScore >= 40) return RiskLevel.HIGH;
        if (riskScore >= 20) return RiskLevel.MODERATE;
        return RiskLevel.LOW;
    }
    
    // Additional helper methods...
    
    private List<String> generatePersonalizedRecommendations(Student student, 
            double overallGrade, double engagementScore, RiskLevel riskLevel) {
        List<String> recommendations = new ArrayList<>();
        
        if (overallGrade < 70) {
            recommendations.add("Schedule office hours with instructor to review challenging concepts");
            recommendations.add("Form a study group with classmates");
        }
        
        if (engagementScore < 50) {
            recommendations.add("Increase participation in course discussions and forums");
            recommendations.add("Attend optional review sessions and workshops");
        }
        
        if (riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL) {
            recommendations.add("Consider tutoring services through the academic success center");
            recommendations.add("Meet with academic advisor to discuss support options");
        }
        
        return recommendations;
    }
    
    private List<PerformanceTrend> analyzePerformanceTrends(String studentId, int days) {
        // Simulate trend analysis
        return List.of(
            new PerformanceTrend("Assignments", TrendDirection.IMPROVING, 5.2),
            new PerformanceTrend("Quiz Performance", TrendDirection.STABLE, 0.8),
            new PerformanceTrend("Engagement", TrendDirection.DECLINING, -2.1)
        );
    }
    
    // Simulation methods for demo data
    private List<Double> simulateAssignmentData() {
        return List.of(78.5, 82.1, 75.3, 88.7, 91.2, 85.8, 79.4);
    }
    
    private List<Double> simulateQuizData() {
        return List.of(85.0, 92.5, 78.0, 88.5, 95.0);
    }
    
    private Map<String, Integer> simulateEngagementData() {
        return Map.of("logins", 45, "forumPosts", 12, "helpRequests", 3);
    }
    
    private Double simulateParticipationData() {
        return 87.5; // Participation score out of 100
    }
    
    // Widget creation methods
    private AnalyticsWidget createGradeOverviewWidget(String studentId) {
        return new AnalyticsWidget("grade-overview", "Grade Overview", 
                Map.of("currentGrade", 84.2, "targetGrade", 90.0), WidgetType.CHART);
    }
    
    private AnalyticsWidget createProgressTrackingWidget(String studentId) {
        return new AnalyticsWidget("progress-tracking", "Progress Tracking",
                Map.of("completedAssignments", 7, "totalAssignments", 10), WidgetType.PROGRESS_BAR);
    }
    
    private AnalyticsWidget createUpcomingAssignmentsWidget(String studentId) {
        return new AnalyticsWidget("upcoming-assignments", "Upcoming Assignments",
                Map.of("assignments", List.of("Final Project", "Quiz 6", "Lab Report 8")), WidgetType.LIST);
    }
    
    private AnalyticsWidget createPerformanceTrendsWidget(String studentId) {
        return new AnalyticsWidget("performance-trends", "Performance Trends",
                Map.of("trend", "improving", "change", "+5.2%"), WidgetType.TREND_CHART);
    }
    
    private AnalyticsWidget createAtRiskAlertWidget(String studentId, RiskLevel riskLevel) {
        return new AnalyticsWidget("at-risk-alert", "Academic Alert",
                Map.of("riskLevel", riskLevel.toString(), "priority", "high"), WidgetType.ALERT);
    }
    
    private AnalyticsWidget createRecommendationsWidget(String studentId) {
        return new AnalyticsWidget("recommendations", "Personalized Recommendations",
                Map.of("recommendations", List.of("Schedule office hours", "Join study group")), WidgetType.LIST);
    }
    
    private List<AnalyticsWidget> applyWidgetPreferences(List<AnalyticsWidget> widgets, 
            Map<String, Object> preferences) {
        // Apply user preferences for widget ordering and visibility
        if (preferences == null || preferences.isEmpty()) {
            return widgets;
        }
        
        // Simulate preference application
        return widgets.stream()
                .filter(widget -> !Boolean.FALSE.equals(preferences.get(widget.getId() + ".visible")))
                .collect(Collectors.toList());
    }
    
    private Map<String, Double> generatePeerComparisons(String studentId, double overallGrade) {
        return Map.of(
            "classAverage", 81.3,
            "percentile", 67.5,
            "aboveAverage", overallGrade > 81.3
        );
    }
    
    // Export methods (simplified implementations)
    private byte[] exportToCsv(StudentProgressAnalytics analytics) {
        return "CSV export data".getBytes();
    }
    
    private byte[] exportToPdf(StudentProgressAnalytics analytics) {
        return "PDF export data".getBytes();
    }
    
    private byte[] exportToJson(StudentProgressAnalytics analytics) {
        return "JSON export data".getBytes();
    }
    
    private byte[] exportToExcel(StudentProgressAnalytics analytics) {
        return "Excel export data".getBytes();
    }
    
    // Enums for type safety
    public enum RiskLevel { LOW, MODERATE, HIGH, CRITICAL }
    public enum ExportFormat { CSV, PDF, JSON, EXCEL }
    public enum TrendDirection { IMPROVING, STABLE, DECLINING }
    public enum WidgetType { CHART, PROGRESS_BAR, LIST, TREND_CHART, ALERT }
    
    // Inner classes for data structure
    public static class PerformanceTrend {
        private final String metric;
        private final TrendDirection direction;
        private final double changeValue;
        
        public PerformanceTrend(String metric, TrendDirection direction, double changeValue) {
            this.metric = metric;
            this.direction = direction;
            this.changeValue = changeValue;
        }
        
        // Getters
        public String getMetric() { return metric; }
        public TrendDirection getDirection() { return direction; }
        public double getChangeValue() { return changeValue; }
    }
}