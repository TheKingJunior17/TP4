package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a request for reviewer status submitted by a student.
 * This class encapsulates all information related to a reviewer permission request
 * including submission details, status, and tracking information.
 * 
 * @author TP4 Team - Model Design Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class ReviewerRequest {
    
    /** Unique identifier for the reviewer request */
    private Long requestId;
    
    /** ID of the student who submitted the request */
    private String studentId;
    
    /** Name of the student submitting the request */
    private String studentName;
    
    /** Email address of the requesting student */
    private String studentEmail;
    
    /** Timestamp when the request was submitted */
    private LocalDateTime submissionDate;
    
    /** Current status of the request */
    private RequestStatus status;
    
    /** Additional information or justification provided by the student */
    private String requestReason;
    
    /** ID of the staff member who processed the request (if processed) */
    private String processedByStaffId;
    
    /** Timestamp when the request was processed (if applicable) */
    private LocalDateTime processedDate;
    
    /** Staff member's comments or decision rationale */
    private String staffComments;
    
    /**
     * Enumeration of possible request statuses.
     */
    public enum RequestStatus {
        PENDING("Pending Review"),
        APPROVED("Approved"),
        DENIED("Denied"),
        WITHDRAWN("Withdrawn by Student");
        
        private final String displayName;
        
        RequestStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Default constructor for framework compatibility.
     */
    public ReviewerRequest() {
        // Default constructor for JPA/framework use
    }
    
    /**
     * Constructor for creating a new reviewer request.
     * 
     * @param studentId the ID of the requesting student
     * @param studentName the name of the requesting student
     * @param studentEmail the email of the requesting student
     * @param requestReason the justification for the request
     */
    public ReviewerRequest(String studentId, String studentName, String studentEmail, String requestReason) {
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.studentName = Objects.requireNonNull(studentName, "Student name cannot be null");
        this.studentEmail = Objects.requireNonNull(studentEmail, "Student email cannot be null");
        this.requestReason = requestReason;
        this.submissionDate = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }
    
    /**
     * Processes the request with a staff decision.
     * 
     * @param status the decision status (APPROVED or DENIED)
     * @param staffId the ID of the staff member making the decision
     * @param comments optional comments from the staff member
     * @throws IllegalArgumentException if status is not APPROVED or DENIED
     */
    public void processRequest(RequestStatus status, String staffId, String comments) {
        if (status != RequestStatus.APPROVED && status != RequestStatus.DENIED) {
            throw new IllegalArgumentException("Status must be APPROVED or DENIED");
        }
        
        if (this.status != RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot process a request that is not pending");
        }
        
        this.status = status;
        this.processedByStaffId = Objects.requireNonNull(staffId, "Staff ID cannot be null");
        this.staffComments = comments;
        this.processedDate = LocalDateTime.now();
    }
    
    /**
     * Checks if the request is within the specified date range.
     * 
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return true if the submission date falls within the range
     */
    public boolean isWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null || submissionDate == null) {
            return false;
        }
        
        return !submissionDate.isBefore(startDate) && !submissionDate.isAfter(endDate);
    }
    
    /**
     * Checks if the request is currently pending.
     * 
     * @return true if the request status is PENDING
     */
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }
    
    /**
     * Checks if the request has been processed.
     * 
     * @return true if the request has been approved or denied
     */
    public boolean isProcessed() {
        return status == RequestStatus.APPROVED || status == RequestStatus.DENIED;
    }
    
    // Getters and Setters
    
    public Long getRequestId() {
        return requestId;
    }
    
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getStudentEmail() {
        return studentEmail;
    }
    
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public String getRequestReason() {
        return requestReason;
    }
    
    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }
    
    public String getProcessedByStaffId() {
        return processedByStaffId;
    }
    
    public void setProcessedByStaffId(String processedByStaffId) {
        this.processedByStaffId = processedByStaffId;
    }
    
    public LocalDateTime getProcessedDate() {
        return processedDate;
    }
    
    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }
    
    public String getStaffComments() {
        return staffComments;
    }
    
    public void setStaffComments(String staffComments) {
        this.staffComments = staffComments;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ReviewerRequest that = (ReviewerRequest) obj;
        return Objects.equals(requestId, that.requestId) &&
               Objects.equals(studentId, that.studentId) &&
               Objects.equals(submissionDate, that.submissionDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(requestId, studentId, submissionDate);
    }
    
    @Override
    public String toString() {
        return String.format(
            "ReviewerRequest{id=%d, studentId='%s', studentName='%s', status=%s, submissionDate=%s}",
            requestId, studentId, studentName, status, submissionDate
        );
    }
}