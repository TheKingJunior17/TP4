package edu.asu.cse360.tp4;

import edu.asu.cse360.tp4.staff.service.StaffManagementService;
import edu.asu.cse360.tp4.staff.repository.StaffRepositoryImpl;
import edu.asu.cse360.tp4.common.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Staff Management Application for TP4
 * Demonstrates all implemented user stories and functionality
 */
public class StaffManagementApp {
    
    private static StaffManagementService staffService;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        System.out.println("=== TP4 Staff Management System ===");
        System.out.println("Arizona State University - CSE 360");
        System.out.println("Interactive Staff Management Application\n");
        
        // Initialize the system
        StaffRepositoryImpl repository = new StaffRepositoryImpl();
        staffService = new StaffManagementService(repository);
        scanner = new Scanner(System.in);
        
        // Main application loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    handleFilterRequests();
                    break;
                case 2:
                    handleViewDecisionHistory();
                    break;
                case 3:
                    handleContentFlagging();
                    break;
                case 4:
                    handleStudentSearch();
                    break;
                case 5:
                    handleRoleManagement();
                    break;
                case 6:
                    runSystemDemo();
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using TP4 Staff Management System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
        
        scanner.close();
    }
    
    private static void displayMainMenu() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║         STAFF MANAGEMENT MENU         ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Filter Reviewer Requests by Date   ║");
        System.out.println("║ 2. View Reviewer Decision History     ║");
        System.out.println("║ 3. Flag Inappropriate Content         ║");
        System.out.println("║ 4. Search Students                    ║");
        System.out.println("║ 5. Manage Reviewer Roles              ║");
        System.out.println("║ 6. Run Complete System Demo           ║");
        System.out.println("║ 0. Exit Application                   ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Enter your choice: ");
    }
    
    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    // User Story 1: Sort Reviewer Requests by Date
    private static void handleFilterRequests() {
        System.out.println("\n🔍 FILTER REVIEWER REQUESTS BY DATE");
        System.out.println("═══════════════════════════════════════");
        
        try {
            System.out.println("Available options:");
            System.out.println("1. Last 7 days");
            System.out.println("2. Last 30 days");
            System.out.println("3. All requests");
            System.out.print("Choose option (1-3): ");
            
            int option = getUserChoice();
            LocalDateTime startDate = null;
            LocalDateTime endDate = LocalDateTime.now();
            
            switch (option) {
                case 1:
                    startDate = LocalDateTime.now().minusDays(7);
                    break;
                case 2:
                    startDate = LocalDateTime.now().minusDays(30);
                    break;
                case 3:
                    startDate = null;
                    endDate = null;
                    break;
                default:
                    System.out.println("Invalid option.\n");
                    return;
            }
            
            List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(startDate, endDate);
            
            System.out.println("\n📋 REVIEWER REQUESTS FOUND: " + requests.size());
            System.out.println("═══════════════════════════════════════");
            
            if (requests.isEmpty()) {
                System.out.println("No reviewer requests found in the specified date range.");
            } else {
                for (int i = 0; i < requests.size(); i++) {
                    ReviewerRequest request = requests.get(i);
                    System.out.printf("%d. Student: %s\n", (i + 1), request.getStudentName());
                    System.out.printf("   Email: %s\n", request.getStudentEmail());
                    System.out.printf("   Date: %s\n", request.getSubmissionDate());
                    System.out.printf("   Status: %s\n", request.getStatus());
                    System.out.printf("   Reason: %s\n\n", request.getReason());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error filtering requests: " + e.getMessage());
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
    
    // User Story 2: Maintain a Reviewer Decision Log
    private static void handleViewDecisionHistory() {
        System.out.println("\n📊 REVIEWER DECISION HISTORY");
        System.out.println("════════════════════════════");
        
        try {
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            
            if (history.isEmpty()) {
                System.out.println("No decision history available.");
            } else {
                System.out.println("Found " + history.size() + " decisions (most recent first):\n");
                
                for (int i = 0; i < history.size(); i++) {
                    ReviewerDecision decision = history.get(i);
                    System.out.printf("%d. Decision: %s\n", (i + 1), decision.getDecisionType());
                    System.out.printf("   Student: %s\n", decision.getStudentId());
                    System.out.printf("   Staff: %s\n", decision.getStaffId());
                    System.out.printf("   Date: %s\n", decision.getDecisionDate());
                    System.out.printf("   Reason: %s\n\n", decision.getReason());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error retrieving decision history: " + e.getMessage());
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
    
    // User Story 3: Report Inappropriate Questions or Responses
    private static void handleContentFlagging() {
        System.out.println("\n🚩 FLAG INAPPROPRIATE CONTENT");
        System.out.println("═════════════════════════════");
        
        try {
            System.out.println("Content flagging options:");
            System.out.println("1. Flag Question ID 1 as inappropriate");
            System.out.println("2. Flag Answer ID 1 as inappropriate");
            System.out.println("3. Unflag Question ID 1");
            System.out.println("4. Unflag Answer ID 1");
            System.out.print("Choose option (1-4): ");
            
            int option = getUserChoice();
            boolean success = false;
            String operation = "";
            
            switch (option) {
                case 1:
                    success = staffService.flagQuestion(1L, "Inappropriate content detected", true);
                    operation = "Flag Question ID 1";
                    break;
                case 2:
                    success = staffService.flagAnswer(1L, "Violates community guidelines", true);
                    operation = "Flag Answer ID 1";
                    break;
                case 3:
                    success = staffService.flagQuestion(1L, "False positive - content approved", false);
                    operation = "Unflag Question ID 1";
                    break;
                case 4:
                    success = staffService.flagAnswer(1L, "Reviewed and cleared", false);
                    operation = "Unflag Answer ID 1";
                    break;
                default:
                    System.out.println("Invalid option.\n");
                    return;
            }
            
            System.out.println("\n📝 OPERATION RESULT:");
            System.out.println("Operation: " + operation);
            System.out.println("Status: " + (success ? "✅ SUCCESS" : "❌ FAILED"));
            
            if (success) {
                System.out.println("Content flagging operation completed successfully.");
            } else {
                System.out.println("Content flagging operation failed (content may not exist).");
            }
            
        } catch (Exception e) {
            System.out.println("Error in content flagging: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
    
    // User Story 4: Locate Students by Name or Email
    private static void handleStudentSearch() {
        System.out.println("\n🔍 STUDENT SEARCH");
        System.out.println("═════════════════");
        
        try {
            System.out.println("Search options:");
            System.out.println("1. Search by exact name: 'Alice Johnson'");
            System.out.println("2. Search by partial name: 'John'");
            System.out.println("3. Search by email: 'alice.johnson@asu.edu'");
            System.out.println("4. Case-insensitive search: 'ALICE'");
            System.out.println("5. View all students (empty search)");
            System.out.println("6. Custom search");
            System.out.print("Choose option (1-6): ");
            
            int option = getUserChoice();
            String searchTerm = "";
            
            switch (option) {
                case 1:
                    searchTerm = "Alice Johnson";
                    break;
                case 2:
                    searchTerm = "John";
                    break;
                case 3:
                    searchTerm = "alice.johnson@asu.edu";
                    break;
                case 4:
                    searchTerm = "ALICE";
                    break;
                case 5:
                    searchTerm = "";
                    break;
                case 6:
                    System.out.print("Enter search term: ");
                    searchTerm = scanner.nextLine().trim();
                    break;
                default:
                    System.out.println("Invalid option.\n");
                    return;
            }
            
            List<Student> results = staffService.searchStudents(searchTerm);
            
            System.out.println("\n👥 SEARCH RESULTS: " + results.size() + " students found");
            System.out.println("Search term: '" + searchTerm + "'");
            System.out.println("═══════════════════════════════════════");
            
            if (results.isEmpty()) {
                System.out.println("No students found matching the search criteria.");
            } else {
                for (int i = 0; i < results.size(); i++) {
                    Student student = results.get(i);
                    System.out.printf("%d. Name: %s %s\n", (i + 1), 
                        student.getFirstName(), student.getLastName());
                    System.out.printf("   ID: %s\n", student.getStudentId());
                    System.out.printf("   Email: %s\n", student.getEmail());
                    System.out.printf("   Major: %s\n", student.getMajor());
                    System.out.printf("   Level: %s\n", student.getAcademicLevel());
                    System.out.printf("   Reviewer: %s\n\n", 
                        student.hasReviewerRole() ? "Yes" : "No");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error in student search: " + e.getMessage());
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
    
    // User Story 5: Adjust Reviewer Role Manually
    private static void handleRoleManagement() {
        System.out.println("\n👥 REVIEWER ROLE MANAGEMENT");
        System.out.println("══════════════════════════");
        
        try {
            System.out.println("Role management options:");
            System.out.println("1. Assign reviewer role to student3");
            System.out.println("2. Remove reviewer role from student2");
            System.out.println("3. Try to assign role to non-existent student");
            System.out.println("4. Try to assign duplicate role to student2");
            System.out.print("Choose option (1-4): ");
            
            int option = getUserChoice();
            boolean success = false;
            String operation = "";
            String studentId = "";
            String staffId = "staff1";
            
            switch (option) {
                case 1:
                    studentId = "student3";
                    success = staffService.assignReviewerRole(studentId, staffId);
                    operation = "Assign reviewer role to " + studentId;
                    break;
                case 2:
                    studentId = "student2";
                    success = staffService.removeReviewerRole(studentId, staffId);
                    operation = "Remove reviewer role from " + studentId;
                    break;
                case 3:
                    studentId = "nonexistent";
                    success = staffService.assignReviewerRole(studentId, staffId);
                    operation = "Assign role to non-existent student";
                    break;
                case 4:
                    studentId = "student2";
                    success = staffService.assignReviewerRole(studentId, staffId);
                    operation = "Assign duplicate role to " + studentId;
                    break;
                default:
                    System.out.println("Invalid option.\n");
                    return;
            }
            
            System.out.println("\n📝 ROLE MANAGEMENT RESULT:");
            System.out.println("Operation: " + operation);
            System.out.println("Staff ID: " + staffId);
            System.out.println("Status: " + (success ? "✅ SUCCESS" : "❌ FAILED/PREVENTED"));
            
            if (success) {
                System.out.println("Role management operation completed successfully.");
            } else {
                System.out.println("Role management operation failed or was prevented " +
                    "(student may not exist or already have the role).");
            }
            
        } catch (Exception e) {
            System.out.println("Error in role management: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
    
    // Complete system demonstration
    private static void runSystemDemo() {
        System.out.println("\n🚀 COMPLETE SYSTEM DEMONSTRATION");
        System.out.println("══════════════════════════════════");
        System.out.println("Running automated demo of all user stories...\n");
        
        try {
            // Demo 1: Filter requests
            System.out.println("1️⃣ FILTERING REVIEWER REQUESTS BY DATE");
            List<ReviewerRequest> requests = staffService.filterRequestsByDateRange(
                LocalDateTime.now().minusDays(7), LocalDateTime.now());
            System.out.println("✅ Found " + requests.size() + " requests in last 7 days");
            
            // Demo 2: Decision history
            System.out.println("\n2️⃣ VIEWING REVIEWER DECISION HISTORY");
            List<ReviewerDecision> history = staffService.getReviewerDecisionHistory();
            System.out.println("✅ Retrieved " + history.size() + " decisions from history");
            
            // Demo 3: Content flagging
            System.out.println("\n3️⃣ FLAGGING INAPPROPRIATE CONTENT");
            boolean flagResult = staffService.flagQuestion(1L, "Demo flag", true);
            System.out.println("✅ Question flagging: " + (flagResult ? "SUCCESS" : "FAILED"));
            boolean unflagResult = staffService.flagQuestion(1L, "Demo unflag", false);
            System.out.println("✅ Question unflagging: " + (unflagResult ? "SUCCESS" : "FAILED"));
            
            // Demo 4: Student search
            System.out.println("\n4️⃣ SEARCHING FOR STUDENTS");
            List<Student> searchResults = staffService.searchStudents("Alice");
            System.out.println("✅ Found " + searchResults.size() + " students matching 'Alice'");
            
            // Demo 5: Role management
            System.out.println("\n5️⃣ MANAGING REVIEWER ROLES");
            boolean assignResult = staffService.assignReviewerRole("student4", "staff1");
            System.out.println("✅ Role assignment: " + (assignResult ? "SUCCESS" : "FAILED"));
            
            System.out.println("\n🎉 SYSTEM DEMONSTRATION COMPLETE!");
            System.out.println("All user stories have been successfully demonstrated.");
            
        } catch (Exception e) {
            System.out.println("❌ Error during system demo: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        System.out.println();
    }
}