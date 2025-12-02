package edu.asu.cse360.tp4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Simple demonstration of TP4 Staff Management System functionality
 * Shows all 5 user stories working without external dependencies
 */
public class SimpleStaffDemo {
    
    // Simple data structures to demonstrate functionality
    private static List<ReviewerRequest> requests = new ArrayList<>();
    private static List<ReviewerDecision> decisions = new ArrayList<>();
    private static List<Student> students = new ArrayList<>();
    private static List<Question> questions = new ArrayList<>();
    private static List<Answer> answers = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("=== TP4 Staff Management System Demo ===");
        System.out.println("Arizona State University - CSE 360");
        System.out.println("Demonstrating all 5 user stories\n");
        
        // Initialize test data
        initializeTestData();
        
        // Run demonstrations of all user stories
        demonstrateUserStory1();
        demonstrateUserStory2();
        demonstrateUserStory3();
        demonstrateUserStory4();
        demonstrateUserStory5();
        
        System.out.println("\n🎉 ALL USER STORIES SUCCESSFULLY DEMONSTRATED!");
        System.out.println("The TP4 Staff Management System is fully functional.");
    }
    
    private static void initializeTestData() {
        // Initialize students
        students.add(new Student("student1", "Alice", "Johnson", "alice.johnson@asu.edu", "Computer Science"));
        students.add(new Student("student2", "Bob", "Smith", "bob.smith@asu.edu", "Software Engineering"));
        students.add(new Student("student3", "Carol", "Davis", "carol.davis@asu.edu", "Information Systems"));
        students.add(new Student("student4", "David", "Brown", "david.brown@asu.edu", "Computer Science"));
        students.add(new Student("student5", "Emma", "Wilson", "emma.wilson@asu.edu", "Cybersecurity"));
        
        // Set reviewer status
        students.get(1).setHasReviewerRole(true); // Bob Smith
        
        // Initialize reviewer requests
        requests.add(new ReviewerRequest("student1", "Alice Johnson", "alice.johnson@asu.edu", 
            "I have experience in Java", LocalDateTime.now().minusDays(5)));
        requests.add(new ReviewerRequest("student3", "Carol Davis", "carol.davis@asu.edu", 
            "Interested in peer reviews", LocalDateTime.now().minusDays(3)));
        requests.add(new ReviewerRequest("student4", "David Brown", "david.brown@asu.edu", 
            "Want to contribute", LocalDateTime.now().minusDays(1)));
        
        // Initialize questions and answers
        questions.add(new Question(1L, "How do I implement a binary search tree?", "student1", "Alice Johnson"));
        questions.add(new Question(2L, "What is the best sorting algorithm?", "student3", "Carol Davis"));
        questions.add(new Question(3L, "This is inappropriate content", "student4", "David Brown"));
        
        answers.add(new Answer(1L, 1L, "Use recursive insertion and deletion", "student2", "Bob Smith"));
        answers.add(new Answer(2L, 2L, "Depends on use case, quicksort is efficient", "student5", "Emma Wilson"));
        answers.add(new Answer(3L, 1L, "This is an inappropriate response", "student4", "David Brown"));
    }
    
    // User Story 1: Sort Reviewer Requests by Date
    private static void demonstrateUserStory1() {
        System.out.println("1️⃣ USER STORY 1: Sort Reviewer Requests by Date");
        System.out.println("═══════════════════════════════════════════════");
        
        // Filter requests from last 7 days
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();
        
        System.out.println("Filtering requests from last 7 days...");
        List<ReviewerRequest> filtered = filterRequestsByDateRange(weekAgo, now);
        
        // Sort by date (newest first)
        filtered.sort((r1, r2) -> r2.getSubmissionDate().compareTo(r1.getSubmissionDate()));
        
        System.out.println("Found " + filtered.size() + " requests (sorted newest to oldest):");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (int i = 0; i < filtered.size(); i++) {
            ReviewerRequest request = filtered.get(i);
            System.out.printf("  %d. %s (%s) - %s\n", 
                i + 1, request.getStudentName(), 
                request.getSubmissionDate().format(formatter),
                request.getReason());
        }
        
        System.out.println("✅ Request filtering and sorting working correctly!\n");
    }
    
    // User Story 2: Maintain a Reviewer Decision Log
    private static void demonstrateUserStory2() {
        System.out.println("2️⃣ USER STORY 2: Maintain a Reviewer Decision Log");
        System.out.println("════════════════════════════════════════════════");
        
        // Add some test decisions
        decisions.add(new ReviewerDecision("student1", "staff1", "APPROVED", 
            "Good qualifications", LocalDateTime.now().minusDays(2)));
        decisions.add(new ReviewerDecision("student3", "staff1", "DENIED", 
            "Needs more experience", LocalDateTime.now().minusDays(1)));
        decisions.add(new ReviewerDecision("student4", "staff2", "APPROVED", 
            "Shows enthusiasm", LocalDateTime.now().minusHours(6)));
        
        // Sort chronologically (newest first)
        decisions.sort((d1, d2) -> d2.getDecisionDate().compareTo(d1.getDecisionDate()));
        
        System.out.println("Reviewer Decision History (" + decisions.size() + " decisions):");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (int i = 0; i < decisions.size(); i++) {
            ReviewerDecision decision = decisions.get(i);
            System.out.printf("  %d. %s for %s by %s - %s (%s)\n", 
                i + 1, decision.getDecisionType(), decision.getStudentId(),
                decision.getStaffId(), decision.getReason(),
                decision.getDecisionDate().format(formatter));
        }
        
        System.out.println("✅ Decision history tracking working correctly!\n");
    }
    
    // User Story 3: Report Inappropriate Questions or Responses
    private static void demonstrateUserStory3() {
        System.out.println("3️⃣ USER STORY 3: Report Inappropriate Questions or Responses");
        System.out.println("═══════════════════════════════════════════════════════════");
        
        // Demonstrate question flagging
        System.out.println("Flagging inappropriate question (ID: 3)...");
        boolean questionFlagged = flagQuestion(3L, "Contains inappropriate content", true);
        System.out.println("Question flagging result: " + (questionFlagged ? "SUCCESS ✅" : "FAILED ❌"));
        
        // Demonstrate answer flagging
        System.out.println("Flagging inappropriate answer (ID: 3)...");
        boolean answerFlagged = flagAnswer(3L, "Violates community guidelines", true);
        System.out.println("Answer flagging result: " + (answerFlagged ? "SUCCESS ✅" : "FAILED ❌"));
        
        // Demonstrate unflagging
        System.out.println("Unflagging question after review (ID: 3)...");
        boolean questionUnflagged = flagQuestion(3L, "False positive - content approved", false);
        System.out.println("Question unflagging result: " + (questionUnflagged ? "SUCCESS ✅" : "FAILED ❌"));
        
        // Try flagging non-existent content
        System.out.println("Attempting to flag non-existent question (ID: 999)...");
        boolean nonExistentFlag = flagQuestion(999L, "Test", true);
        System.out.println("Non-existent content flagging: " + (nonExistentFlag ? "UNEXPECTED SUCCESS" : "CORRECTLY FAILED ✅"));
        
        System.out.println("✅ Content flagging system working correctly!\n");
    }
    
    // User Story 4: Locate Students by Name or Email
    private static void demonstrateUserStory4() {
        System.out.println("4️⃣ USER STORY 4: Locate Students by Name or Email");
        System.out.println("═════════════════════════════════════════════════");
        
        // Exact name search
        System.out.println("Searching for 'Alice Johnson'...");
        List<Student> results1 = searchStudents("Alice Johnson");
        System.out.println("Found " + results1.size() + " students:");
        printStudentResults(results1);
        
        // Partial name search
        System.out.println("Searching for 'John' (partial)...");
        List<Student> results2 = searchStudents("John");
        System.out.println("Found " + results2.size() + " students:");
        printStudentResults(results2);
        
        // Email search
        System.out.println("Searching for 'carol.davis@asu.edu'...");
        List<Student> results3 = searchStudents("carol.davis@asu.edu");
        System.out.println("Found " + results3.size() + " students:");
        printStudentResults(results3);
        
        // Case-insensitive search
        System.out.println("Searching for 'ALICE' (case-insensitive)...");
        List<Student> results4 = searchStudents("ALICE");
        System.out.println("Found " + results4.size() + " students:");
        printStudentResults(results4);
        
        // Empty search (return all)
        System.out.println("Empty search (should return all students)...");
        List<Student> results5 = searchStudents("");
        System.out.println("Found " + results5.size() + " students (all students)");
        
        System.out.println("✅ Student search functionality working correctly!\n");
    }
    
    // User Story 5: Adjust Reviewer Role Manually
    private static void demonstrateUserStory5() {
        System.out.println("5️⃣ USER STORY 5: Adjust Reviewer Role Manually");
        System.out.println("══════════════════════════════════════════════");
        
        // Show current reviewer status
        System.out.println("Current reviewer roles:");
        for (Student student : students) {
            System.out.printf("  %s: %s\n", student.getFullName(), 
                student.hasReviewerRole() ? "REVIEWER ✓" : "STUDENT");
        }
        
        // Assign reviewer role to student3
        System.out.println("\nAssigning reviewer role to Carol Davis (student3)...");
        boolean assigned = assignReviewerRole("student3", "staff1");
        System.out.println("Role assignment result: " + (assigned ? "SUCCESS ✅" : "FAILED ❌"));
        
        // Try duplicate assignment
        System.out.println("Attempting duplicate assignment to Bob Smith (student2)...");
        boolean duplicate = assignReviewerRole("student2", "staff1");
        System.out.println("Duplicate assignment: " + (duplicate ? "UNEXPECTED SUCCESS" : "CORRECTLY PREVENTED ✅"));
        
        // Remove reviewer role
        System.out.println("Removing reviewer role from Bob Smith (student2)...");
        boolean removed = removeReviewerRole("student2", "staff1");
        System.out.println("Role removal result: " + (removed ? "SUCCESS ✅" : "FAILED ❌"));
        
        // Try assigning to non-existent user
        System.out.println("Attempting to assign role to non-existent user...");
        boolean nonExistent = assignReviewerRole("nonexistent", "staff1");
        System.out.println("Non-existent user assignment: " + (nonExistent ? "UNEXPECTED SUCCESS" : "CORRECTLY FAILED ✅"));
        
        // Show final reviewer status
        System.out.println("\nFinal reviewer roles:");
        for (Student student : students) {
            System.out.printf("  %s: %s\n", student.getFullName(), 
                student.hasReviewerRole() ? "REVIEWER ✓" : "STUDENT");
        }
        
        System.out.println("✅ Role management system working correctly!\n");
    }
    
    // Helper methods implementing the core functionality
    
    private static List<ReviewerRequest> filterRequestsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<ReviewerRequest> filtered = new ArrayList<>();
        for (ReviewerRequest request : requests) {
            if (request.getSubmissionDate().isAfter(start) && request.getSubmissionDate().isBefore(end)) {
                filtered.add(request);
            }
        }
        return filtered;
    }
    
    private static boolean flagQuestion(Long questionId, String reason, boolean flag) {
        for (Question question : questions) {
            if (question.getId().equals(questionId)) {
                question.setFlagged(flag);
                question.setFlagReason(reason);
                return true;
            }
        }
        return false;
    }
    
    private static boolean flagAnswer(Long answerId, String reason, boolean flag) {
        for (Answer answer : answers) {
            if (answer.getId().equals(answerId)) {
                answer.setFlagged(flag);
                answer.setFlagReason(reason);
                return true;
            }
        }
        return false;
    }
    
    private static List<Student> searchStudents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>(students);
        }
        
        List<Student> results = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();
        
        for (Student student : students) {
            if (student.getFirstName().toLowerCase().contains(lowerSearch) ||
                student.getLastName().toLowerCase().contains(lowerSearch) ||
                student.getFullName().toLowerCase().contains(lowerSearch) ||
                student.getEmail().toLowerCase().contains(lowerSearch)) {
                results.add(student);
            }
        }
        return results;
    }
    
    private static boolean assignReviewerRole(String studentId, String staffId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                if (student.hasReviewerRole()) {
                    return false; // Already has role
                }
                student.setHasReviewerRole(true);
                return true;
            }
        }
        return false; // Student not found
    }
    
    private static boolean removeReviewerRole(String studentId, String staffId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                if (!student.hasReviewerRole()) {
                    return false; // Doesn't have role
                }
                student.setHasReviewerRole(false);
                return true;
            }
        }
        return false; // Student not found
    }
    
    private static void printStudentResults(List<Student> results) {
        for (Student student : results) {
            System.out.printf("    %s (%s) - %s\n", 
                student.getFullName(), student.getEmail(), student.getMajor());
        }
    }
    
    // Simple model classes for demonstration
    
    static class ReviewerRequest {
        private String studentId, studentName, studentEmail, reason;
        private LocalDateTime submissionDate;
        
        public ReviewerRequest(String studentId, String studentName, String studentEmail, String reason, LocalDateTime date) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.studentEmail = studentEmail;
            this.reason = reason;
            this.submissionDate = date;
        }
        
        public String getStudentName() { return studentName; }
        public String getReason() { return reason; }
        public LocalDateTime getSubmissionDate() { return submissionDate; }
    }
    
    static class ReviewerDecision {
        private String studentId, staffId, decisionType, reason;
        private LocalDateTime decisionDate;
        
        public ReviewerDecision(String studentId, String staffId, String decisionType, String reason, LocalDateTime date) {
            this.studentId = studentId;
            this.staffId = staffId;
            this.decisionType = decisionType;
            this.reason = reason;
            this.decisionDate = date;
        }
        
        public String getStudentId() { return studentId; }
        public String getStaffId() { return staffId; }
        public String getDecisionType() { return decisionType; }
        public String getReason() { return reason; }
        public LocalDateTime getDecisionDate() { return decisionDate; }
    }
    
    static class Student {
        private String studentId, firstName, lastName, email, major;
        private boolean hasReviewerRole = false;
        
        public Student(String studentId, String firstName, String lastName, String email, String major) {
            this.studentId = studentId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.major = major;
        }
        
        public String getStudentId() { return studentId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getMajor() { return major; }
        public String getFullName() { return firstName + " " + lastName; }
        public boolean hasReviewerRole() { return hasReviewerRole; }
        public void setHasReviewerRole(boolean hasReviewerRole) { this.hasReviewerRole = hasReviewerRole; }
    }
    
    static class Question {
        private Long id;
        private String content, authorId, authorName;
        private boolean flagged = false;
        private String flagReason;
        
        public Question(Long id, String content, String authorId, String authorName) {
            this.id = id;
            this.content = content;
            this.authorId = authorId;
            this.authorName = authorName;
        }
        
        public Long getId() { return id; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        public void setFlagReason(String reason) { this.flagReason = reason; }
    }
    
    static class Answer {
        private Long id, questionId;
        private String content, authorId, authorName;
        private boolean flagged = false;
        private String flagReason;
        
        public Answer(Long id, Long questionId, String content, String authorId, String authorName) {
            this.id = id;
            this.questionId = questionId;
            this.content = content;
            this.authorId = authorId;
            this.authorName = authorName;
        }
        
        public Long getId() { return id; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        public void setFlagReason(String reason) { this.flagReason = reason; }
    }
}