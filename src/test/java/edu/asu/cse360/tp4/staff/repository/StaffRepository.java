package edu.asu.cse360.tp4.staff.repository;

import edu.asu.cse360.tp4.common.model.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for staff-related data access operations.
 * 
 * This interface defines the contract for data access operations
 * related to staff management functionality including reviewer requests,
 * decisions, students, questions, and answers.
 * 
 * @author TP4 Team - Data Access Layer Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public interface StaffRepository {
    
    /**
     * Retrieves all reviewer requests from the database.
     * 
     * @return list of all reviewer requests
     * @throws SQLException if database operation fails
     */
    List<ReviewerRequest> getAllReviewerRequests() throws SQLException;
    
    /**
     * Retrieves a specific reviewer request by ID.
     * 
     * @param requestId the ID of the request to retrieve
     * @return the reviewer request or null if not found
     * @throws SQLException if database operation fails
     */
    ReviewerRequest getReviewerRequestById(Long requestId) throws SQLException;
    
    /**
     * Updates an existing reviewer request.
     * 
     * @param request the request to update
     * @throws SQLException if database operation fails
     */
    void updateReviewerRequest(ReviewerRequest request) throws SQLException;
    
    /**
     * Retrieves all reviewer decision history records.
     * 
     * @return list of all decision records
     * @throws SQLException if database operation fails
     */
    List<ReviewerDecision> getReviewerDecisionHistory() throws SQLException;
    
    /**
     * Saves a new reviewer decision record.
     * 
     * @param decision the decision record to save
     * @throws SQLException if database operation fails
     */
    void saveReviewerDecision(ReviewerDecision decision) throws SQLException;
    
    /**
     * Retrieves a question by ID.
     * 
     * @param questionId the ID of the question to retrieve
     * @return the question or null if not found
     * @throws SQLException if database operation fails
     */
    Question getQuestionById(Long questionId) throws SQLException;
    
    /**
     * Updates an existing question.
     * 
     * @param question the question to update
     * @throws SQLException if database operation fails
     */
    void updateQuestion(Question question) throws SQLException;
    
    /**
     * Retrieves an answer by ID.
     * 
     * @param answerId the ID of the answer to retrieve
     * @return the answer or null if not found
     * @throws SQLException if database operation fails
     */
    Answer getAnswerById(Long answerId) throws SQLException;
    
    /**
     * Updates an existing answer.
     * 
     * @param answer the answer to update
     * @throws SQLException if database operation fails
     */
    void updateAnswer(Answer answer) throws SQLException;
    
    /**
     * Retrieves all students from the database.
     * 
     * @return list of all students
     * @throws SQLException if database operation fails
     */
    List<Student> getAllStudents() throws SQLException;
    
    /**
     * Retrieves a specific student by ID.
     * 
     * @param studentId the ID of the student to retrieve
     * @return the student or null if not found
     * @throws SQLException if database operation fails
     */
    Student getStudentById(String studentId) throws SQLException;
    
    /**
     * Updates an existing student record.
     * 
     * @param student the student to update
     * @throws SQLException if database operation fails
     */
    void updateStudent(Student student) throws SQLException;
    
    /**
     * Logs a role change operation.
     * 
     * @param studentId the student whose role was changed
     * @param staffId the staff member who made the change
     * @param action the action taken
     * @param reason the reason for the change
     * @param timestamp when the change occurred
     * @throws SQLException if database operation fails
     */
    void logRoleChange(String studentId, String staffId, String action, String reason, LocalDateTime timestamp) throws SQLException;
    
    /**
     * Gets the display name for a staff member.
     * 
     * @param staffId the staff ID
     * @return the staff member's name
     * @throws SQLException if database operation fails
     */
    String getStaffName(String staffId) throws SQLException;
}