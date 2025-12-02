package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a question in the system that can be flagged for inappropriate content.
 * This class manages question data and flagging status for staff review.
 * 
 * @author TP4 Team - Model Design Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class Question {
    
    /** Unique identifier for the question */
    private Long questionId;
    
    /** The actual question text */
    private String questionText;
    
    /** ID of the student who posted the question */
    private String authorId;
    
    /** Name of the author for display purposes */
    private String authorName;
    
    /** Course or subject the question relates to */
    private String subject;
    
    /** Timestamp when the question was posted */
    private LocalDateTime creationDate;
    
    /** Whether this question has been flagged as inappropriate */
    private boolean flagged;
    
    /** ID of the staff member who flagged the question */
    private String flaggedByStaffId;
    
    /** Timestamp when the question was flagged */
    private LocalDateTime flaggedDate;
    
    /** Reason for flagging the question */
    private String flagReason;
    
    /** Current status of the question */
    private QuestionStatus status;
    
    /** Number of views this question has received */
    private Integer viewCount;
    
    /** Number of answers this question has received */
    private Integer answerCount;
    
    /**
     * Enumeration for question status.
     */
    public enum QuestionStatus {
        ACTIVE("Active"),
        FLAGGED("Flagged for Review"),
        HIDDEN("Hidden"),
        DELETED("Deleted"),
        RESOLVED("Resolved");
        
        private final String displayName;
        
        QuestionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Default constructor for framework compatibility.
     */
    public Question() {
        // Default constructor for JPA/framework use
    }
    
    /**
     * Constructor for creating a new question.
     * 
     * @param questionText the content of the question
     * @param authorId the ID of the student posting the question
     * @param authorName the name of the author
     * @param subject the subject or course the question relates to
     */
    public Question(String questionText, String authorId, String authorName, String subject) {
        this.questionText = Objects.requireNonNull(questionText, "Question text cannot be null");
        this.authorId = Objects.requireNonNull(authorId, "Author ID cannot be null");
        this.authorName = Objects.requireNonNull(authorName, "Author name cannot be null");
        this.subject = subject;
        this.creationDate = LocalDateTime.now();
        this.flagged = false;
        this.status = QuestionStatus.ACTIVE;
        this.viewCount = 0;
        this.answerCount = 0;
    }
    
    /**
     * Flags this question as inappropriate.
     * 
     * @param staffId the ID of the staff member flagging the question
     * @param reason the reason for flagging
     * @throws IllegalStateException if the question is already flagged
     */
    public void flagAsInappropriate(String staffId, String reason) {
        if (this.flagged) {
            throw new IllegalStateException("Question is already flagged");
        }
        
        this.flagged = true;
        this.flaggedByStaffId = Objects.requireNonNull(staffId, "Staff ID cannot be null");
        this.flagReason = reason;
        this.flaggedDate = LocalDateTime.now();
        this.status = QuestionStatus.FLAGGED;
    }
    
    /**
     * Removes the inappropriate flag from this question.
     * 
     * @throws IllegalStateException if the question is not flagged
     */
    public void removeFlagAsInappropriate() {
        if (!this.flagged) {
            throw new IllegalStateException("Question is not flagged");
        }
        
        this.flagged = false;
        this.flaggedByStaffId = null;
        this.flagReason = null;
        this.flaggedDate = null;
        this.status = QuestionStatus.ACTIVE;
    }
    
    /**
     * Increments the view count for this question.
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1 : this.viewCount + 1;
    }
    
    /**
     * Increments the answer count for this question.
     */
    public void incrementAnswerCount() {
        this.answerCount = (this.answerCount == null) ? 1 : this.answerCount + 1;
    }
    
    /**
     * Decrements the answer count for this question.
     */
    public void decrementAnswerCount() {
        if (this.answerCount != null && this.answerCount > 0) {
            this.answerCount--;
        }
    }
    
    /**
     * Checks if this question is currently active.
     * 
     * @return true if the question status is ACTIVE
     */
    public boolean isActive() {
        return status == QuestionStatus.ACTIVE;
    }
    
    /**
     * Gets a truncated version of the question text for display.
     * 
     * @param maxLength maximum length of the truncated text
     * @return truncated question text
     */
    public String getTruncatedText(int maxLength) {
        if (questionText == null || questionText.length() <= maxLength) {
            return questionText;
        }
        return questionText.substring(0, maxLength) + "...";
    }
    
    /**
     * Gets a formatted display string for the question.
     * 
     * @return formatted question summary
     */
    public String getDisplaySummary() {
        return String.format("[%s] %s - by %s (%d views, %d answers)", 
                           subject != null ? subject : "General",
                           getTruncatedText(50),
                           authorName,
                           viewCount != null ? viewCount : 0,
                           answerCount != null ? answerCount : 0);
    }
    
    // Getters and Setters
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
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
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
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
    
    public QuestionStatus getStatus() {
        return status;
    }
    
    public void setStatus(QuestionStatus status) {
        this.status = status;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getAnswerCount() {
        return answerCount;
    }
    
    public void setAnswerCount(Integer answerCount) {
        this.answerCount = answerCount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question question = (Question) obj;
        return Objects.equals(questionId, question.questionId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(questionId);
    }
    
    @Override
    public String toString() {
        return String.format(
            "Question{id=%d, author='%s', subject='%s', flagged=%s, status=%s}",
            questionId, authorName, subject, flagged, status
        );
    }
}