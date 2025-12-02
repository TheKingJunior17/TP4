package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an answer to a question in the system that can be flagged for inappropriate content.
 * This class manages answer data and flagging status for staff review.
 * 
 * @author TP4 Team - Model Design Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class Answer {
    
    /** Unique identifier for the answer */
    private Long answerId;
    
    /** ID of the question this answer belongs to */
    private Long questionId;
    
    /** The actual answer text */
    private String answerText;
    
    /** ID of the student who posted the answer */
    private String authorId;
    
    /** Name of the author for display purposes */
    private String authorName;
    
    /** Timestamp when the answer was posted */
    private LocalDateTime creationDate;
    
    /** Whether this answer has been flagged as inappropriate */
    private boolean flagged;
    
    /** ID of the staff member who flagged the answer */
    private String flaggedByStaffId;
    
    /** Timestamp when the answer was flagged */
    private LocalDateTime flaggedDate;
    
    /** Reason for flagging the answer */
    private String flagReason;
    
    /** Current status of the answer */
    private AnswerStatus status;
    
    /** Whether this answer has been marked as helpful */
    private boolean helpful;
    
    /** Number of upvotes this answer has received */
    private Integer upvotes;
    
    /** Number of downvotes this answer has received */
    private Integer downvotes;
    
    /**
     * Enumeration for answer status.
     */
    public enum AnswerStatus {
        ACTIVE("Active"),
        FLAGGED("Flagged for Review"),
        HIDDEN("Hidden"),
        DELETED("Deleted"),
        APPROVED("Approved");
        
        private final String displayName;
        
        AnswerStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Default constructor for framework compatibility.
     */
    public Answer() {
        // Default constructor for JPA/framework use
    }
    
    /**
     * Constructor for creating a new answer.
     * 
     * @param questionId the ID of the question being answered
     * @param answerText the content of the answer
     * @param authorId the ID of the student posting the answer
     * @param authorName the name of the author
     */
    public Answer(Long questionId, String answerText, String authorId, String authorName) {
        this.questionId = Objects.requireNonNull(questionId, "Question ID cannot be null");
        this.answerText = Objects.requireNonNull(answerText, "Answer text cannot be null");
        this.authorId = Objects.requireNonNull(authorId, "Author ID cannot be null");
        this.authorName = Objects.requireNonNull(authorName, "Author name cannot be null");
        this.creationDate = LocalDateTime.now();
        this.flagged = false;
        this.status = AnswerStatus.ACTIVE;
        this.helpful = false;
        this.upvotes = 0;
        this.downvotes = 0;
    }
    
    /**
     * Flags this answer as inappropriate.
     * 
     * @param staffId the ID of the staff member flagging the answer
     * @param reason the reason for flagging
     * @throws IllegalStateException if the answer is already flagged
     */
    public void flagAsInappropriate(String staffId, String reason) {
        if (this.flagged) {
            throw new IllegalStateException("Answer is already flagged");
        }
        
        this.flagged = true;
        this.flaggedByStaffId = Objects.requireNonNull(staffId, "Staff ID cannot be null");
        this.flagReason = reason;
        this.flaggedDate = LocalDateTime.now();
        this.status = AnswerStatus.FLAGGED;
    }
    
    /**
     * Removes the inappropriate flag from this answer.
     * 
     * @throws IllegalStateException if the answer is not flagged
     */
    public void removeFlagAsInappropriate() {
        if (!this.flagged) {
            throw new IllegalStateException("Answer is not flagged");
        }
        
        this.flagged = false;
        this.flaggedByStaffId = null;
        this.flagReason = null;
        this.flaggedDate = null;
        this.status = AnswerStatus.ACTIVE;
    }
    
    /**
     * Marks this answer as helpful.
     */
    public void markAsHelpful() {
        this.helpful = true;
    }
    
    /**
     * Removes the helpful marking from this answer.
     */
    public void unmarkAsHelpful() {
        this.helpful = false;
    }
    
    /**
     * Adds an upvote to this answer.
     */
    public void addUpvote() {
        this.upvotes = (this.upvotes == null) ? 1 : this.upvotes + 1;
    }
    
    /**
     * Adds a downvote to this answer.
     */
    public void addDownvote() {
        this.downvotes = (this.downvotes == null) ? 1 : this.downvotes + 1;
    }
    
    /**
     * Removes an upvote from this answer.
     */
    public void removeUpvote() {
        if (this.upvotes != null && this.upvotes > 0) {
            this.upvotes--;
        }
    }
    
    /**
     * Removes a downvote from this answer.
     */
    public void removeDownvote() {
        if (this.downvotes != null && this.downvotes > 0) {
            this.downvotes--;
        }
    }
    
    /**
     * Calculates the net score (upvotes - downvotes) for this answer.
     * 
     * @return the net score
     */
    public int getNetScore() {
        int up = (upvotes != null) ? upvotes : 0;
        int down = (downvotes != null) ? downvotes : 0;
        return up - down;
    }
    
    /**
     * Checks if this answer is currently active.
     * 
     * @return true if the answer status is ACTIVE
     */
    public boolean isActive() {
        return status == AnswerStatus.ACTIVE;
    }
    
    /**
     * Gets a truncated version of the answer text for display.
     * 
     * @param maxLength maximum length of the truncated text
     * @return truncated answer text
     */
    public String getTruncatedText(int maxLength) {
        if (answerText == null || answerText.length() <= maxLength) {
            return answerText;
        }
        return answerText.substring(0, maxLength) + "...";
    }
    
    /**
     * Gets a formatted display string for the answer.
     * 
     * @return formatted answer summary
     */
    public String getDisplaySummary() {
        return String.format("%s - by %s (Score: %+d, %s)", 
                           getTruncatedText(50),
                           authorName,
                           getNetScore(),
                           helpful ? "Helpful" : "Not marked helpful");
    }
    
    // Getters and Setters
    
    public Long getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getAnswerText() {
        return answerText;
    }
    
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public boolean isFlagged() {
        return flagged;
    }
    
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
    
    public String getFlaggedByStaffId() {
        return flaggedByStaffId;
    }
    
    public void setFlaggedByStaffId(String flaggedByStaffId) {
        this.flaggedByStaffId = flaggedByStaffId;
    }
    
    public LocalDateTime getFlaggedDate() {
        return flaggedDate;
    }
    
    public void setFlaggedDate(LocalDateTime flaggedDate) {
        this.flaggedDate = flaggedDate;
    }
    
    public String getFlagReason() {
        return flagReason;
    }
    
    public void setFlagReason(String flagReason) {
        this.flagReason = flagReason;
    }
    
    public AnswerStatus getStatus() {
        return status;
    }
    
    public void setStatus(AnswerStatus status) {
        this.status = status;
    }
    
    public boolean isHelpful() {
        return helpful;
    }
    
    public void setHelpful(boolean helpful) {
        this.helpful = helpful;
    }
    
    public Integer getUpvotes() {
        return upvotes;
    }
    
    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }
    
    public Integer getDownvotes() {
        return downvotes;
    }
    
    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Answer answer = (Answer) obj;
        return Objects.equals(answerId, answer.answerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(answerId);
    }
    
    @Override
    public String toString() {
        return String.format(
            "Answer{id=%d, questionId=%d, author='%s', flagged=%s, status=%s, helpful=%s}",
            answerId, questionId, authorName, flagged, status, helpful
        );
    }
}