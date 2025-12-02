package edu.asu.cse360.tp4.common.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a student in the system with comprehensive information
 * including personal details, academic information, and system permissions.
 * 
 * @author TP4 Team - Model Design Lead
 * @version 1.0.0
 * @since TP4 Implementation Phase
 */
public class Student {
    
    /** Unique identifier for the student */
    private String studentId;
    
    /** Student's first name */
    private String firstName;
    
    /** Student's last name */
    private String lastName;
    
    /** Student's email address */
    private String email;
    
    /** Student's phone number (optional) */
    private String phoneNumber;
    
    /** Student's enrollment status */
    private EnrollmentStatus enrollmentStatus;
    
    /** Student's academic level */
    private AcademicLevel academicLevel;
    
    /** Student's major or program of study */
    private String major;
    
    /** Date when the student was registered in the system */
    private LocalDateTime registrationDate;
    
    /** Last login timestamp */
    private LocalDateTime lastLoginDate;
    
    /** Whether the student has reviewer privileges */
    private boolean hasReviewerRole;
    
    /** Set of courses the student is enrolled in */
    private Set<String> enrolledCourses;
    
    /** Student's current GPA (optional) */
    private Double gpa;
    
    /** Number of credit hours completed */
    private Integer creditHours;
    
    /** Expected graduation date */
    private LocalDateTime expectedGraduation;
    
    /**
     * Enumeration for student enrollment status.
     */
    public enum EnrollmentStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        GRADUATED("Graduated"),
        WITHDRAWN("Withdrawn"),
        SUSPENDED("Suspended");
        
        private final String displayName;
        
        EnrollmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Enumeration for academic levels.
     */
    public enum AcademicLevel {
        FRESHMAN("Freshman"),
        SOPHOMORE("Sophomore"),
        JUNIOR("Junior"),
        SENIOR("Senior"),
        GRADUATE("Graduate"),
        DOCTORAL("Doctoral");
        
        private final String displayName;
        
        AcademicLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Default constructor for framework compatibility.
     */
    public Student() {
        this.enrolledCourses = new HashSet<>();
    }
    
    /**
     * Constructor for creating a new student.
     * 
     * @param studentId unique identifier for the student
     * @param firstName student's first name
     * @param lastName student's last name
     * @param email student's email address
     */
    public Student(String studentId, String firstName, String lastName, String email) {
        this.studentId = Objects.requireNonNull(studentId, "Student ID cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.enrollmentStatus = EnrollmentStatus.ACTIVE;
        this.academicLevel = AcademicLevel.FRESHMAN;
        this.registrationDate = LocalDateTime.now();
        this.enrolledCourses = new HashSet<>();
        this.hasReviewerRole = false;
    }
    
    /**
     * Gets the student's full name.
     * 
     * @return formatted full name (firstName lastName)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Checks if the student matches the given search term.
     * Searches in first name, last name, email, and student ID.
     * 
     * @param searchTerm the term to search for (case insensitive)
     * @return true if the student matches the search term
     */
    public boolean matchesSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true; // Empty search matches all
        }
        
        String lowercaseSearch = searchTerm.toLowerCase();
        
        return (firstName != null && firstName.toLowerCase().contains(lowercaseSearch)) ||
               (lastName != null && lastName.toLowerCase().contains(lowercaseSearch)) ||
               (email != null && email.toLowerCase().contains(lowercaseSearch)) ||
               (studentId != null && studentId.toLowerCase().contains(lowercaseSearch)) ||
               (getFullName().toLowerCase().contains(lowercaseSearch));
    }
    
    /**
     * Grants reviewer role to the student.
     * 
     * @throws IllegalStateException if student is not active
     */
    public void grantReviewerRole() {
        if (enrollmentStatus != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot grant reviewer role to inactive student");
        }
        this.hasReviewerRole = true;
    }
    
    /**
     * Removes reviewer role from the student.
     */
    public void revokeReviewerRole() {
        this.hasReviewerRole = false;
    }
    
    /**
     * Checks if the student is currently active.
     * 
     * @return true if enrollment status is ACTIVE
     */
    public boolean isActive() {
        return enrollmentStatus == EnrollmentStatus.ACTIVE;
    }
    
    /**
     * Gets a formatted display string for the student.
     * 
     * @return formatted string with name, ID, and status
     */
    public String getDisplayInfo() {
        return String.format("%s (%s) - %s", 
                           getFullName(), 
                           studentId, 
                           enrollmentStatus.getDisplayName());
    }
    
    // Getters and Setters
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }
    
    public void setEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }
    
    public AcademicLevel getAcademicLevel() {
        return academicLevel;
    }
    
    public void setAcademicLevel(AcademicLevel academicLevel) {
        this.academicLevel = academicLevel;
    }
    
    public String getMajor() {
        return major;
    }
    
    public void setMajor(String major) {
        this.major = major;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public boolean isHasReviewerRole() {
        return hasReviewerRole;
    }
    
    public void setHasReviewerRole(boolean hasReviewerRole) {
        this.hasReviewerRole = hasReviewerRole;
    }
    
    public Set<String> getEnrolledCourses() {
        return new HashSet<>(enrolledCourses);
    }
    
    public void setEnrolledCourses(Set<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses != null ? new HashSet<>(enrolledCourses) : new HashSet<>();
    }
    
    public Double getGpa() {
        return gpa;
    }
    
    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }
    
    public Integer getCreditHours() {
        return creditHours;
    }
    
    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }
    
    public LocalDateTime getExpectedGraduation() {
        return expectedGraduation;
    }
    
    public void setExpectedGraduation(LocalDateTime expectedGraduation) {
        this.expectedGraduation = expectedGraduation;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return Objects.equals(studentId, student.studentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
    
    @Override
    public String toString() {
        return String.format(
            "Student{id='%s', name='%s', email='%s', status=%s, hasReviewerRole=%s}",
            studentId, getFullName(), email, enrollmentStatus, hasReviewerRole
        );
    }
}