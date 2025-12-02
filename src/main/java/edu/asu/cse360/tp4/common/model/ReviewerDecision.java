package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a decision made by staff regarding a reviewer request.
 * This class maintains a chronological history of all approval and denial decisions
 * including the rationale and staff member information.
 * 
 * @author TP4 Team - Model Design Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class ReviewerDecision {
    
    /** Unique identifier for the decision record */
    private Long decisionId;
    
    /** Reference to the original reviewer request */
    private Long requestId;
    
    /** ID of the student whose request was decided upon */
    private String studentId;
    
    /** Name of the student for quick reference */
    private String studentName;
    
    /** The decision made (APPROVED or DENIED) */
    private ReviewerRequest.RequestStatus decision;
    
    /** ID of the staff member who made the decision */
    private String staffId;
    
    /** Name of the staff member who made the decision */
    private String staffName;
    
    /** Timestamp when the decision was made */
    private LocalDateTime decisionDate;
    
    /** Detailed rationale or comments for the decision */
    private String decisionRationale;
    
    /** Additional context or notes about the decision */
    private String additionalNotes;
    
    /** Priority level of the decision (for urgent cases) */
    private DecisionPriority priority;
    
    /**
     * Enumeration for decision priority levels.
     */
    public enum DecisionPriority {
        LOW("Low Priority"),
        NORMAL("Normal Priority"),
        HIGH("High Priority"),
        URGENT("Urgent");
        
        private final String displayName;
        
        DecisionPriority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Default constructor for framework compatibility.
     */
    public ReviewerDecision() {
        // Default constructor for JPA/framework use
    }
    
    /**
     * Constructor for creating a new reviewer decision.
     * 
     * @param requestId the ID of the associated request
     * @param studentId the ID of the student
     * @param studentName the name of the student
     * @param decision the decision made (APPROVED or DENIED)
     * @param staffId the ID of the staff member making the decision
     * @param staffName the name of the staff member
     * @param decisionRationale the rationale for the decision
     */
    public ReviewerDecision(Long requestId, String studentId, String studentName, 
                          ReviewerRequest.RequestStatus decision, String staffId, 
                          String staffName, String decisionRationale) {
        this.requestId = Objects.requireNonNull(requestId, "Request ID cannot be null");
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.studentName = Objects.requireNonNull(studentName, "Student name cannot be null");
        this.decision = Objects.requireNonNull(decision, "Decision cannot be null");
        this.staffId = Objects.requireNonNull(staffId, "Staff ID cannot be null");
        this.staffName = Objects.requireNonNull(staffName, "Staff name cannot be null");
        this.decisionRationale = decisionRationale;
        this.decisionDate = LocalDateTime.now();
        this.priority = DecisionPriority.NORMAL;
        
        // Validate that decision is either APPROVED or DENIED
        if (decision != ReviewerRequest.RequestStatus.APPROVED && 
            decision != ReviewerRequest.RequestStatus.DENIED) {
            throw new IllegalArgumentException("Decision must be APPROVED or DENIED");
        }
    }
    
    /**
     * Checks if this decision was made within the specified date range.
     * 
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return true if the decision date falls within the range
     */
    public boolean isWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null || decisionDate == null) {
            return false;
        }
        
        return !decisionDate.isBefore(startDate) && !decisionDate.isAfter(endDate);
    }
    
    /**
     * Checks if this is an approval decision.
     * 
     * @return true if the decision is APPROVED
     */
    public boolean isApproval() {
        return decision == ReviewerRequest.RequestStatus.APPROVED;
    }
    
    /**
     * Checks if this is a denial decision.
     * 
     * @return true if the decision is DENIED
     */
    public boolean isDenial() {
        return decision == ReviewerRequest.RequestStatus.DENIED;
    }
    
    /**
     * Gets a formatted string representation of the decision for display.
     * 
     * @return formatted decision string
     */
    public String getFormattedDecision() {
        return String.format("%s - %s by %s on %s", 
                           studentName, 
                           decision.getDisplayName(), 
                           staffName, 
                           decisionDate.toLocalDate());
    }
    
    /**
     * Updates the additional notes for this decision.
     * 
     * @param notes additional notes to add
     */
    public void addAdditionalNotes(String notes) {
        if (this.additionalNotes == null || this.additionalNotes.isEmpty()) {
            this.additionalNotes = notes;
        } else {
            this.additionalNotes += "\n" + LocalDateTime.now() + ": " + notes;
        }
    }
    
    // Getters and Setters
    
    public Long getDecisionId() {
        return decisionId;
    }
    
    public void setDecisionId(Long decisionId) {
        this.decisionId = decisionId;
    }
    
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
    
    public ReviewerRequest.RequestStatus getDecision() {
        return decision;
    }
    
    public void setDecision(ReviewerRequest.RequestStatus decision) {
        this.decision = decision;
    }
    
    public String getStaffId() {
        return staffId;
    }
    
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public LocalDateTime getDecisionDate() {
        return decisionDate;
    }
    
    public void setDecisionDate(LocalDateTime decisionDate) {
        this.decisionDate = decisionDate;
    }
    
    public String getDecisionRationale() {
        return decisionRationale;
    }
    
    public void setDecisionRationale(String decisionRationale) {
        this.decisionRationale = decisionRationale;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
    
    public DecisionPriority getPriority() {
        return priority;
    }
    
    public void setPriority(DecisionPriority priority) {
        this.priority = priority;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ReviewerDecision that = (ReviewerDecision) obj;
        return Objects.equals(decisionId, that.decisionId) &&
               Objects.equals(requestId, that.requestId) &&
               Objects.equals(decisionDate, that.decisionDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(decisionId, requestId, decisionDate);
    }
    
    @Override
    public String toString() {
        return String.format(
            "ReviewerDecision{id=%d, requestId=%d, studentId='%s', decision=%s, staffId='%s', date=%s}",
            decisionId, requestId, studentId, decision, staffId, decisionDate
        );
    }
}