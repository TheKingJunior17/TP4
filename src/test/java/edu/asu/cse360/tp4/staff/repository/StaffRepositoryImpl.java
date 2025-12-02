package edu.asu.cse360.tp4.staff.repository;

import edu.asu.cse360.tp4.common.model.*;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of StaffRepository for testing and development purposes.
 * 
 * This implementation uses concurrent collections to simulate database operations
 * and provides a complete implementation for testing the service layer.
 * In a production environment, this would be replaced with a JPA or JDBC implementation.
 * 
 * @author TP4 Team - Data Access Layer Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
@Repository
public class StaffRepositoryImpl implements StaffRepository {
    
    // In-memory storage using concurrent collections for thread safety
    private final Map<Long, ReviewerRequest> reviewerRequests = new ConcurrentHashMap<>();
    private final Map<Long, ReviewerDecision> reviewerDecisions = new ConcurrentHashMap<>();
    private final Map<Long, Question> questions = new ConcurrentHashMap<>();
    private final Map<Long, Answer> answers = new ConcurrentHashMap<>();
    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final List<RoleChangeLog> roleChangeLogs = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, String> staffNames = new ConcurrentHashMap<>();
    
    // ID generators
    private final AtomicLong requestIdGenerator = new AtomicLong(1);
    private final AtomicLong decisionIdGenerator = new AtomicLong(1);
    private final AtomicLong questionIdGenerator = new AtomicLong(1);
    private final AtomicLong answerIdGenerator = new AtomicLong(1);
    
    // Flag to simulate database failures for testing
    private boolean simulateFailure = false;
    
    /**
     * Constructor that initializes test data.
     */
    public StaffRepositoryImpl() {
        initializeTestData();
    }
    
    @Override
    public List<ReviewerRequest> getAllReviewerRequests() throws SQLException {
        checkForSimulatedFailure();
        return new ArrayList<>(reviewerRequests.values());
    }
    
    @Override
    public ReviewerRequest getReviewerRequestById(Long requestId) throws SQLException {
        checkForSimulatedFailure();
        return reviewerRequests.get(requestId);
    }
    
    @Override
    public void updateReviewerRequest(ReviewerRequest request) throws SQLException {
        checkForSimulatedFailure();
        if (request.getRequestId() == null) {
            request.setRequestId(requestIdGenerator.getAndIncrement());
        }
        reviewerRequests.put(request.getRequestId(), request);
    }
    
    @Override
    public List<ReviewerDecision> getReviewerDecisionHistory() throws SQLException {
        checkForSimulatedFailure();
        return new ArrayList<>(reviewerDecisions.values());
    }
    
    @Override
    public void saveReviewerDecision(ReviewerDecision decision) throws SQLException {
        checkForSimulatedFailure();
        if (decision.getDecisionId() == null) {
            decision.setDecisionId(decisionIdGenerator.getAndIncrement());
        }
        reviewerDecisions.put(decision.getDecisionId(), decision);
    }
    
    @Override
    public Question getQuestionById(Long questionId) throws SQLException {
        checkForSimulatedFailure();
        return questions.get(questionId);
    }
    
    @Override
    public void updateQuestion(Question question) throws SQLException {
        checkForSimulatedFailure();
        if (question.getQuestionId() == null) {
            question.setQuestionId(questionIdGenerator.getAndIncrement());
        }
        questions.put(question.getQuestionId(), question);
    }
    
    @Override
    public Answer getAnswerById(Long answerId) throws SQLException {
        checkForSimulatedFailure();
        return answers.get(answerId);
    }
    
    @Override
    public void updateAnswer(Answer answer) throws SQLException {
        checkForSimulatedFailure();
        if (answer.getAnswerId() == null) {
            answer.setAnswerId(answerIdGenerator.getAndIncrement());
        }
        answers.put(answer.getAnswerId(), answer);
    }
    
    @Override
    public List<Student> getAllStudents() throws SQLException {
        checkForSimulatedFailure();
        return new ArrayList<>(students.values());
    }
    
    @Override
    public Student getStudentById(String studentId) throws SQLException {
        checkForSimulatedFailure();
        return students.get(studentId);
    }
    
    @Override
    public void updateStudent(Student student) throws SQLException {
        checkForSimulatedFailure();
        students.put(student.getStudentId(), student);
    }
    
    @Override
    public void logRoleChange(String studentId, String staffId, String action, String reason, LocalDateTime timestamp) throws SQLException {
        checkForSimulatedFailure();
        RoleChangeLog log = new RoleChangeLog(studentId, staffId, action, reason, timestamp);
        roleChangeLogs.add(log);
    }
    
    @Override
    public String getStaffName(String staffId) throws SQLException {
        checkForSimulatedFailure();
        return staffNames.getOrDefault(staffId, staffId);
    }
    
    /**
     * Initializes test data for development and testing purposes.
     */
    private void initializeTestData() {
        // Initialize staff names
        staffNames.put("staff1", "John Smith");
        staffNames.put("staff2", "Jane Doe");
        staffNames.put("staff3", "Bob Wilson");
        
        // Initialize students
        Student student1 = new Student("student1", "Alice", "Johnson", "alice.johnson@asu.edu");
        student1.setMajor("Computer Science");
        student1.setAcademicLevel(Student.AcademicLevel.JUNIOR);
        students.put("student1", student1);
        
        Student student2 = new Student("student2", "Bob", "Smith", "bob.smith@asu.edu");
        student2.setMajor("Software Engineering");
        student2.setAcademicLevel(Student.AcademicLevel.SENIOR);
        student2.setHasReviewerRole(true);
        students.put("student2", student2);
        
        Student student3 = new Student("student3", "Carol", "Davis", "carol.davis@asu.edu");
        student3.setMajor("Information Systems");
        student3.setAcademicLevel(Student.AcademicLevel.SOPHOMORE);
        students.put("student3", student3);
        
        Student student4 = new Student("student4", "David", "Brown", "david.brown@asu.edu");
        student4.setMajor("Computer Science");
        student4.setAcademicLevel(Student.AcademicLevel.FRESHMAN);
        students.put("student4", student4);
        
        Student student5 = new Student("student5", "Emma", "Wilson", "emma.wilson@asu.edu");
        student5.setMajor("Cybersecurity");
        student5.setAcademicLevel(Student.AcademicLevel.GRADUATE);
        students.put("student5", student5);
        
        // Initialize reviewer requests
        ReviewerRequest request1 = new ReviewerRequest("student1", "Alice Johnson", "alice.johnson@asu.edu", "I have experience in Java and want to help review code");
        request1.setRequestId(1L);
        request1.setSubmissionDate(LocalDateTime.now().minusDays(5));
        reviewerRequests.put(1L, request1);
        
        ReviewerRequest request2 = new ReviewerRequest("student3", "Carol Davis", "carol.davis@asu.edu", "I'm interested in helping with peer reviews");
        request2.setRequestId(2L);
        request2.setSubmissionDate(LocalDateTime.now().minusDays(3));
        reviewerRequests.put(2L, request2);
        
        ReviewerRequest request3 = new ReviewerRequest("student4", "David Brown", "david.brown@asu.edu", "Want to contribute to the community");
        request3.setRequestId(3L);
        request3.setSubmissionDate(LocalDateTime.now().minusDays(1));
        reviewerRequests.put(3L, request3);
        
        // Initialize questions
        Question question1 = new Question("How do I implement a binary search tree?", "student1", "Alice Johnson", "Data Structures");
        question1.setQuestionId(1L);
        question1.setCreationDate(LocalDateTime.now().minusDays(2));
        questions.put(1L, question1);
        
        Question question2 = new Question("What is the best sorting algorithm?", "student3", "Carol Davis", "Algorithms");
        question2.setQuestionId(2L);
        question2.setCreationDate(LocalDateTime.now().minusDays(1));
        questions.put(2L, question2);
        
        Question question3 = new Question("This is an inappropriate question", "student4", "David Brown", "General");
        question3.setQuestionId(3L);
        question3.setCreationDate(LocalDateTime.now().minusHours(6));
        questions.put(3L, question3);
        
        // Initialize answers
        Answer answer1 = new Answer(1L, "You can implement a BST using recursive insertion and deletion methods", "student2", "Bob Smith");
        answer1.setAnswerId(1L);
        answer1.setCreationDate(LocalDateTime.now().minusDays(1));
        answers.put(1L, answer1);
        
        Answer answer2 = new Answer(2L, "It depends on your use case, but quicksort is generally efficient", "student5", "Emma Wilson");
        answer2.setAnswerId(2L);
        answer2.setCreationDate(LocalDateTime.now().minusHours(12));
        answers.put(2L, answer2);
        
        Answer answer3 = new Answer(1L, "This is an inappropriate answer", "student4", "David Brown");
        answer3.setAnswerId(3L);
        answer3.setCreationDate(LocalDateTime.now().minusHours(3));
        answers.put(3L, answer3);
    }
    
    /**
     * Checks if failure simulation is enabled and throws SQLException if so.
     * 
     * @throws SQLException if failure simulation is enabled
     */
    private void checkForSimulatedFailure() throws SQLException {
        if (simulateFailure) {
            throw new SQLException("Simulated database failure for testing");
        }
    }
    
    /**
     * Enables or disables failure simulation for testing purposes.
     * 
     * @param simulateFailure true to simulate failures, false to disable
     */
    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
    
    /**
     * Gets all role change logs for testing purposes.
     * 
     * @return list of role change logs
     */
    public List<RoleChangeLog> getRoleChangeLogs() {
        return new ArrayList<>(roleChangeLogs);
    }
    
    /**
     * Clears all test data (useful for testing).
     */
    public void clearAllData() {
        reviewerRequests.clear();
        reviewerDecisions.clear();
        questions.clear();
        answers.clear();
        students.clear();
        roleChangeLogs.clear();
    }
    
    /**
     * Adds a question for testing purposes.
     * 
     * @param question the question to add
     */
    public void addQuestion(Question question) {
        if (question.getQuestionId() == null) {
            question.setQuestionId(questionIdGenerator.getAndIncrement());
        }
        questions.put(question.getQuestionId(), question);
    }
    
    /**
     * Adds an answer for testing purposes.
     * 
     * @param answer the answer to add
     */
    public void addAnswer(Answer answer) {
        if (answer.getAnswerId() == null) {
            answer.setAnswerId(answerIdGenerator.getAndIncrement());
        }
        answers.put(answer.getAnswerId(), answer);
    }
    
    /**
     * Adds a reviewer request for testing purposes.
     * 
     * @param request the request to add
     */
    public void addReviewerRequest(ReviewerRequest request) {
        if (request.getRequestId() == null) {
            request.setRequestId(requestIdGenerator.getAndIncrement());
        }
        reviewerRequests.put(request.getRequestId(), request);
    }
    
    /**
     * Inner class to represent role change log entries.
     */
    public static class RoleChangeLog {
        private final String studentId;
        private final String staffId;
        private final String action;
        private final String reason;
        private final LocalDateTime timestamp;
        
        public RoleChangeLog(String studentId, String staffId, String action, String reason, LocalDateTime timestamp) {
            this.studentId = studentId;
            this.staffId = staffId;
            this.action = action;
            this.reason = reason;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getStudentId() { return studentId; }
        public String getStaffId() { return staffId; }
        public String getAction() { return action; }
        public String getReason() { return reason; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}