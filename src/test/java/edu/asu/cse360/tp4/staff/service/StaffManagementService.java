package edu.asu.cse360.tp4.staff.service;

import edu.asu.cse360.tp4.common.model.*;
import edu.asu.cse360.tp4.common.exception.ValidationException;
import edu.asu.cse360.tp4.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.sql.SQLException;

/**
 * Service class for managing staff operations including reviewer request management,
 * decision history tracking, content flagging, student search, and role management.
 * 
 * This service provides comprehensive functionality for staff members to manage
 * the system including processing reviewer requests, maintaining decision logs,
 * flagging inappropriate content, searching for students, and managing reviewer roles.
 * 
 * @author TP4 Team - Service Layer Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
@Service
public class StaffManagementService {
    
    private final StaffRepository staffRepository;
    
    /**
     * Constructor with dependency injection.
     * 
     * @param staffRepository the repository for data access operations
     */
    public StaffManagementService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    
    /**
     * Filters reviewer requests by date range and returns them sorted by date (newest first).
     * 
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return list of requests within the date range, sorted newest to oldest
     * @throws RuntimeException if database operation fails
     */
    public List<ReviewerRequest> filterRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Validate date range
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                return new ArrayList<>(); // Return empty list for invalid range
            }
            
            List<ReviewerRequest> allRequests = staffRepository.getAllReviewerRequests();
            
            return allRequests.stream()
                    .filter(request -> {
                        if (startDate == null && endDate == null) {
                            return true; // No date filtering
                        }
                        return request.isWithinDateRange(startDate, endDate);
                    })
                    .sorted(Comparator.comparing(ReviewerRequest::getSubmissionDate).reversed())
                    .collect(Collectors.toList());
                    
        } catch (SQLException e) {
            throw new RuntimeException("Database error while filtering requests by date range", e);
        }
    }
    
    /**
     * Retrieves the chronological history of reviewer decisions (approvals and denials).
     * 
     * @return list of decisions sorted from most recent to oldest
     * @throws RuntimeException if database operation fails
     */
    public List<ReviewerDecision> getReviewerDecisionHistory() {
        try {
            List<ReviewerDecision> history = staffRepository.getReviewerDecisionHistory();
            
            // Sort by decision date, newest first
            return history.stream()
                    .sorted(Comparator.comparing(ReviewerDecision::getDecisionDate).reversed())
                    .collect(Collectors.toList());
                    
        } catch (SQLException e) {
            throw new RuntimeException("Database error while retrieving reviewer decision history", e);
        }
    }
    
    /**
     * Flags a question as inappropriate for staff review.
     * 
     * @param questionId the ID of the question to flag
     * @param staffId the ID of the staff member flagging the question
     * @param flagged true to flag, false to remove flag
     * @return true if the operation was successful
     * @throws RuntimeException if database operation fails
     */
    public boolean flagQuestion(Long questionId, String staffId, boolean flagged) {
        try {
            validateStaffId(staffId);
            
            if (questionId == null) {
                return false; // Silently handle null question ID
            }
            
            Question question = staffRepository.getQuestionById(questionId);
            if (question == null) {
                return false; // Question doesn't exist, but don't throw error
            }
            
            if (flagged) {
                if (!question.isFlagged()) {
                    question.flagAsInappropriate(staffId, "Flagged by staff for review");
                    staffRepository.updateQuestion(question);
                }
            } else {
                if (question.isFlagged()) {
                    question.removeFlagAsInappropriate();
                    staffRepository.updateQuestion(question);
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error while flagging question", e);
        }
    }
    
    /**
     * Flags an answer as inappropriate for staff review.
     * 
     * @param answerId the ID of the answer to flag
     * @param staffId the ID of the staff member flagging the answer
     * @param flagged true to flag, false to remove flag
     * @return true if the operation was successful
     * @throws RuntimeException if database operation fails
     */
    public boolean flagAnswer(Long answerId, String staffId, boolean flagged) {
        try {
            validateStaffId(staffId);
            
            if (answerId == null) {
                return false; // Silently handle null answer ID
            }
            
            Answer answer = staffRepository.getAnswerById(answerId);
            if (answer == null) {
                return false; // Answer doesn't exist, but don't throw error
            }
            
            if (flagged) {
                if (!answer.isFlagged()) {
                    answer.flagAsInappropriate(staffId, "Flagged by staff for review");
                    staffRepository.updateAnswer(answer);
                }
            } else {
                if (answer.isFlagged()) {
                    answer.removeFlagAsInappropriate();
                    staffRepository.updateAnswer(answer);
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error while flagging answer", e);
        }
    }
    
    /**
     * Searches for students by name or email (case insensitive).
     * 
     * @param searchTerm the term to search for (can be partial)
     * @return list of students matching the search term
     * @throws RuntimeException if database operation fails
     */
    public List<Student> searchStudents(String searchTerm) {
        try {
            List<Student> allStudents = staffRepository.getAllStudents();
            
            // If search term is empty or null, return all students
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return allStudents.stream()
                        .sorted(Comparator.comparing(Student::getLastName)
                                .thenComparing(Student::getFirstName))
                        .collect(Collectors.toList());
            }
            
            // Filter students based on search term (case insensitive)
            return allStudents.stream()
                    .filter(student -> student.matchesSearchTerm(searchTerm))
                    .sorted(Comparator.comparing(Student::getLastName)
                            .thenComparing(Student::getFirstName))
                    .collect(Collectors.toList());
                    
        } catch (SQLException e) {
            throw new RuntimeException("Database error while searching students", e);
        }
    }
    
    /**
     * Grants reviewer role to a student.
     * 
     * @param studentId the ID of the student to grant reviewer role
     * @param staffId the ID of the staff member making the change
     * @return true if the role was granted, false if student already has the role
     * @throws RuntimeException if database operation fails
     */
    public boolean assignReviewerRole(String studentId, String staffId) {
        try {
            validateStaffId(staffId);
            
            if (studentId == null || studentId.trim().isEmpty()) {
                throw new ValidationException("Student ID cannot be null or empty");
            }
            
            Student student = staffRepository.getStudentById(studentId);
            if (student == null) {
                throw new ValidationException("Student not found: " + studentId);
            }
            
            if (student.isHasReviewerRole()) {
                return false; // Student already has reviewer role
            }
            
            student.grantReviewerRole();
            staffRepository.updateStudent(student);
            
            // Log the role assignment
            logRoleChange(studentId, staffId, "ASSIGNED", "Reviewer role granted by staff");
            
            return true;
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error while assigning reviewer role", e);
        }
    }
    
    /**
     * Removes reviewer role from a student.
     * 
     * @param studentId the ID of the student to remove reviewer role from
     * @param staffId the ID of the staff member making the change
     * @return true if the role was removed, false if student didn't have the role
     * @throws RuntimeException if database operation fails
     */
    public boolean removeReviewerRole(String studentId, String staffId) {
        try {
            validateStaffId(staffId);
            
            if (studentId == null || studentId.trim().isEmpty()) {
                throw new ValidationException("Student ID cannot be null or empty");
            }
            
            Student student = staffRepository.getStudentById(studentId);
            if (student == null) {
                throw new ValidationException("Student not found: " + studentId);
            }
            
            if (!student.isHasReviewerRole()) {
                return false; // Student doesn't have reviewer role
            }
            
            student.revokeReviewerRole();
            staffRepository.updateStudent(student);
            
            // Log the role removal
            logRoleChange(studentId, staffId, "REMOVED", "Reviewer role removed by staff");
            
            return true;
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error while removing reviewer role", e);
        }
    }
    
    /**
     * Processes a reviewer request with approval or denial.
     * 
     * @param requestId the ID of the request to process
     * @param approved true for approval, false for denial
     * @param staffId the ID of the staff member processing the request
     * @param comments optional comments from the staff member
     * @return true if the request was processed successfully
     * @throws RuntimeException if database operation fails
     */
    public boolean processReviewerRequest(Long requestId, boolean approved, String staffId, String comments) {
        try {
            validateStaffId(staffId);
            
            ReviewerRequest request = staffRepository.getReviewerRequestById(requestId);
            if (request == null || !request.isPending()) {
                return false;
            }
            
            ReviewerRequest.RequestStatus decision = approved ? 
                    ReviewerRequest.RequestStatus.APPROVED : 
                    ReviewerRequest.RequestStatus.DENIED;
                    
            request.processRequest(decision, staffId, comments);
            staffRepository.updateReviewerRequest(request);
            
            // If approved, grant reviewer role to the student
            if (approved) {
                assignReviewerRole(request.getStudentId(), staffId);
            }
            
            // Create decision record
            ReviewerDecision decisionRecord = new ReviewerDecision(
                requestId,
                request.getStudentId(),
                request.getStudentName(),
                decision,
                staffId,
                getStaffName(staffId),
                comments != null ? comments : (approved ? "Request approved" : "Request denied")
            );
            
            staffRepository.saveReviewerDecision(decisionRecord);
            
            return true;
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error while processing reviewer request", e);
        }
    }
    
    /**
     * Gets statistics about reviewer requests and decisions.
     * 
     * @return map containing various statistics
     */
    public ReviewerStatistics getReviewerStatistics() {
        try {
            List<ReviewerRequest> allRequests = staffRepository.getAllReviewerRequests();
            List<ReviewerDecision> allDecisions = staffRepository.getReviewerDecisionHistory();
            
            return new ReviewerStatistics(allRequests, allDecisions);
            
        } catch (SQLException e) {
            throw new RuntimeException("Database error while retrieving statistics", e);
        }
    }
    
    // Private helper methods
    
    /**
     * Validates that the staff ID is not null or empty.
     * 
     * @param staffId the staff ID to validate
     * @throws ValidationException if staff ID is invalid
     */
    private void validateStaffId(String staffId) {
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new ValidationException("Staff ID cannot be null or empty");
        }
    }
    
    /**
     * Logs a role change operation.
     * 
     * @param studentId the student whose role was changed
     * @param staffId the staff member who made the change
     * @param action the action taken (ASSIGNED or REMOVED)
     * @param reason the reason for the change
     */
    private void logRoleChange(String studentId, String staffId, String action, String reason) {
        try {
            staffRepository.logRoleChange(studentId, staffId, action, reason, LocalDateTime.now());
        } catch (SQLException e) {
            // Log the error but don't fail the main operation
            System.err.println("Failed to log role change: " + e.getMessage());
        }
    }
    
    /**
     * Gets the display name for a staff member.
     * 
     * @param staffId the staff ID
     * @return the staff member's name or the ID if name not found
     */
    private String getStaffName(String staffId) {
        try {
            return staffRepository.getStaffName(staffId);
        } catch (SQLException e) {
            return staffId; // Return ID as fallback
        }
    }
    
    /**
     * Inner class to hold reviewer statistics.
     */
    public static class ReviewerStatistics {
        private final int totalRequests;
        private final int pendingRequests;
        private final int approvedRequests;
        private final int deniedRequests;
        private final int totalDecisions;
        private final LocalDateTime lastDecisionDate;
        
        public ReviewerStatistics(List<ReviewerRequest> requests, List<ReviewerDecision> decisions) {
            this.totalRequests = requests.size();
            this.pendingRequests = (int) requests.stream().filter(ReviewerRequest::isPending).count();
            this.approvedRequests = (int) requests.stream()
                    .filter(r -> r.getStatus() == ReviewerRequest.RequestStatus.APPROVED).count();
            this.deniedRequests = (int) requests.stream()
                    .filter(r -> r.getStatus() == ReviewerRequest.RequestStatus.DENIED).count();
            this.totalDecisions = decisions.size();
            this.lastDecisionDate = decisions.stream()
                    .map(ReviewerDecision::getDecisionDate)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
        
        // Getters for statistics
        public int getTotalRequests() { return totalRequests; }
        public int getPendingRequests() { return pendingRequests; }
        public int getApprovedRequests() { return approvedRequests; }
        public int getDeniedRequests() { return deniedRequests; }
        public int getTotalDecisions() { return totalDecisions; }
        public LocalDateTime getLastDecisionDate() { return lastDecisionDate; }
    }
}